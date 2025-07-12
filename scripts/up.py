#!/usr/bin/env python3

import sys
import subprocess
import shutil
import platform

def docker_compose_installed():
    return shutil.which("docker") is not None

def run_services(services):
    try:
        print(f"\nStarting services: {', '.join(services)}\n")
        subprocess.run(["docker", "compose", "up", "-d", *services], check=True)
    except subprocess.CalledProcessError as e:
        print("Failed to start services. Error:")
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
        print("Usage: python up.py <iam|patient|testorder|all>")
        sys.exit(1)

    target = sys.argv[1].lower()

    services_map = {
        "iam": ["iam-db", "iam-service"],
        "patient": ["patient-db", "patient-service"],
        "testorder": ["testorder-db", "testorder-service"],
        "all": ["iam-db", "iam-service", "patient-db", "patient-service", "testorder-db", "testorder-service"],
    }

    if target in services_map:
        run_services(services_map[target])
    else:
        print(f"Unknown option: '{target}'")
        print("Usage: python up.py <iam|patient|testorder|all>")
        sys.exit(1)

if __name__ == "__main__":
    main()
