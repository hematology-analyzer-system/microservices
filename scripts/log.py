#!/usr/bin/env python3

import subprocess
import sys
import shutil

DOCKER_CMD = shutil.which("docker") or shutil.which("podman")


def docker_installed():
    return DOCKER_CMD is not None


def get_service_container_names(target):
    # Map target arguments to actual Docker container names
    service_map = {
        "iam": ["iam-service"],
        "patient": ["patient-service"],
        "testorder": ["testorder-service"],
        "all": ["iam-service", "patient-service", "testorder-service"],
    }
    return service_map.get(target)


def stream_logs(container_names):
    if not docker_installed():
        print("Docker is not installed or not available in PATH.")
        sys.exit(1)

    print(f"\nStreaming logs for services: {', '.join(container_names)}\n")
    try:
        compose_command = [DOCKER_CMD, "compose", "logs", "-f"] + container_names
        subprocess.run(compose_command, check=True)

    except subprocess.CalledProcessError as e:
        print(f"Failed to stream logs. Error: {e}")
        print("Ensure the services are running and accessible.")
        sys.exit(1)
    except FileNotFoundError:
        print(
            "Docker or Docker Compose command not found. Please ensure they are installed and in your PATH."
        )
        sys.exit(1)


def main():
    if len(sys.argv) != 2:
        print("Usage: python log.py <iam|patient|testorder|all>")
        sys.exit(1)

    target = sys.argv[1].lower()
    container_names = get_service_container_names(target)

    if container_names:
        stream_logs(container_names)
    else:
        print(f"Unknown option: '{target}'")
        print("Usage: python log.py <iam|patient|testorder|all>")
        sys.exit(1)


if __name__ == "__main__":
    main()
