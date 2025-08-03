#!/usr/bin/env python3

import sys
import subprocess
import shutil

DOCKER_CMD = shutil.which("docker") or shutil.which("podman")


def docker_compose_installed():
    return DOCKER_CMD is not None


def reset_service(service_name, containers):
    try:
        print(f"\nResetting {service_name} service...\n")

        # Stop and remove containers
        print("Stopping containers...")
        subprocess.run([DOCKER_CMD, "compose", "stop"] + containers, check=True)

        print("Removing containers...")
        subprocess.run([DOCKER_CMD, "compose", "rm", "-f"] + containers, check=True)

        # Rebuild and start
        print("Rebuilding and starting...")
        subprocess.run(
            [DOCKER_CMD, "compose", "up", "-d", "--build"] + containers, check=True
        )

        print(f"\n✓ {service_name} service reset complete!")

    except subprocess.CalledProcessError as e:
        print(f"Failed to reset {service_name} service. Error:")
        print(e)
        sys.exit(1)
    except FileNotFoundError:
        print("Docker is not installed or not in your PATH.")
        sys.exit(1)


def main():
    if not docker_compose_installed():
        print("Docker is not installed or not available in PATH.")
        sys.exit(1)

    if len(sys.argv) != 2:
        print("Usage: python reset.py <iam|patient|testorder|mongodb|rabbitmq|postgres|all>")
        sys.exit(1)

    target = sys.argv[1].lower()

    services_map = {
        "iam": ["iam-service"],
        "patient": ["patient-service"],
        "testorder": ["testorder-service"],
        "mongodb": ["mongodb"],
        "rabbitmq": ["rabbitmq"],
        "postgres": ["postgres"],
        "all": ["iam-service", "patient-service", "testorder-service", "mongodb", "rabbitmq", "postgres"],
    }

    if target in services_map:
        reset_service(target, services_map[target])
    else:
        print(f"Unknown option: '{target}'")
        print("Usage: python reset.py <iam|patient|testorder|mongodb|rabbitmq|postgres|all>")
        sys.exit(1)


if __name__ == "__main__":
    main()
