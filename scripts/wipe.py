#!/usr/bin/env python3

import subprocess
import sys
import shutil

DOCKER_CMD = shutil.which("docker") or shutil.which("podman")


def docker_installed():
    return shutil.which(DOCKER_CMD) is not None


def confirm_action(prompt):
    while True:
        response = input(f"{prompt} (yes/no): ").lower().strip()
        if response == "yes":
            return True
        elif response == "no":
            return False
        else:
            print("Invalid input. Please type 'yes' or 'no'.")


def stop_all_containers():
    print("Stopping all running Docker containers...")
    try:
        # List all running container IDs and stop them
        container_ids = (
            subprocess.run(
                [DOCKER_CMD, "ps", "-aq"], capture_output=True, text=True, check=True
            )
            .stdout.strip()
            .splitlines()
        )

        if not container_ids:
            print("No running containers found to stop.")
            return

        subprocess.run([DOCKER_CMD, "stop", *container_ids], check=True)
        print("All running containers stopped successfully.")
    except subprocess.CalledProcessError as e:
        print(f"Failed to stop all containers. Error: {e}")
        sys.exit(1)
    except FileNotFoundError:
        print(
            "Docker command not found. Please ensure Docker is installed and in your PATH."
        )
        sys.exit(1)


def run_docker_prune():
    if not docker_installed():
        print("Docker is not installed or not available in PATH.")
        sys.exit(1)

    print(
        "\nWARNING: This will stop all running containers and then remove all stopped containers, all networks not used by at least one container, all dangling images, and optionally all build cache and all dangling volumes."
    )
    if not confirm_action(
        "Are you sure you want to proceed with a full Docker system prune (including volumes and stopping all containers)?"
    ):
        print("Docker system prune cancelled.")
        sys.exit(0)

    stop_all_containers()

    try:
        print("\nPerforming Docker system prune with volumes...")
        subprocess.run(
            [DOCKER_CMD, "system", "prune", "--all", "--volumes", "--force"], check=True
        )
        print("Docker system prune completed successfully.")
    except subprocess.CalledProcessError as e:
        print(f"Failed to perform Docker system prune. Error: {e}")
        sys.exit(1)
    except FileNotFoundError:
        print(
            "Docker command not found. Please ensure Docker is installed and in your PATH."
        )
        sys.exit(1)


def main():
    run_docker_prune()


if __name__ == "__main__":
    main()
