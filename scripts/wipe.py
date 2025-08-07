#!/usr/bin/env python3
import subprocess
import sys
import shutil

DOCKER_CMD = shutil.which("docker") or shutil.which("podman")

# Application services that should be pruned
APP_SERVICES = ["iam-service", "patient-service", "testorder-service"]

# Base images to preserve (build and runtime)
PRESERVE_IMAGES = [
    "postgres:17-alpine",
    "mongo:7",
    "rabbitmq:3-management",
    "maven:3.9.4-eclipse-temurin-21",
    "eclipse-temurin:21-jre-alpine",
]


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


def stop_app_containers():
    print("Stopping application containers...")
    try:
        for service in APP_SERVICES:
            try:
                subprocess.run(
                    [DOCKER_CMD, "stop", service], check=True, capture_output=True
                )
                print(f"Stopped {service}")
            except subprocess.CalledProcessError:
                print(f"Container {service} not running or doesn't exist")
    except FileNotFoundError:
        print(
            "Docker command not found. Please ensure Docker is installed and in your PATH."
        )
        sys.exit(1)


def remove_app_containers():
    print("Removing application containers...")
    try:
        for service in APP_SERVICES:
            try:
                subprocess.run(
                    [DOCKER_CMD, "rm", service], check=True, capture_output=True
                )
                print(f"Removed container {service}")
            except subprocess.CalledProcessError:
                print(f"Container {service} doesn't exist")
    except subprocess.CalledProcessError as e:
        print(f"Failed to remove some containers. Error: {e}")


def remove_app_images():
    print("Removing application images...")
    try:
        # Get all images
        result = subprocess.run(
            [DOCKER_CMD, "images", "--format", "{{.Repository}}:{{.Tag}}\t{{.ID}}"],
            capture_output=True,
            text=True,
            check=True,
        )

        images_to_remove = []
        for line in result.stdout.strip().splitlines():
            if line:
                image_info = line.split("\t")
                if len(image_info) >= 2:
                    image_name = image_info[0]
                    image_id = image_info[1]

                    # Skip base images that should be preserved
                    should_preserve = False
                    for preserve_image in PRESERVE_IMAGES:
                        if image_name == preserve_image or preserve_image in image_name:
                            should_preserve = True
                            break

                    if should_preserve:
                        continue

                    # Check if this is one the app service images
                    for app_service in APP_SERVICES:
                        if app_service in image_name or image_name.startswith(
                            f"{app_service}"
                        ):
                            images_to_remove.append(image_id)
                            print(f"Will remove image: {image_name} ({image_id})")
                            break

        if images_to_remove:
            subprocess.run([DOCKER_CMD, "rmi", *images_to_remove], check=True)
            print(f"Removed {len(images_to_remove)} application images")
        else:
            print("No application images found to remove")

    except subprocess.CalledProcessError as e:
        print(f"Failed to remove application images. Error: {e}")


def cleanup_build_cache():
    print("Cleaning up build cache...")
    try:
        subprocess.run([DOCKER_CMD, "builder", "prune", "--force"], check=True)
        print("Build cache cleaned")
    except subprocess.CalledProcessError as e:
        print(f"Failed to clean build cache. Error: {e}")


def run_selective_cleanup():
    if not docker_installed():
        print("Docker is not installed or not available in PATH.")
        sys.exit(1)

    print("This will:")
    print("- Stop and remove application service containers:", ", ".join(APP_SERVICES))
    print("- Remove application service images")
    print("- Clean build cache")
    print("- Preserve base images:", ", ".join(PRESERVE_IMAGES))

    if not confirm_action("Proceed with selective cleanup?"):
        print("Selective cleanup cancelled.")
        sys.exit(0)

    stop_app_containers()
    remove_app_containers()
    remove_app_images()
    cleanup_build_cache()

    print("\nSelective cleanup completed successfully!")
    print("Infrastructure services and base images have been preserved.")


def main():
    run_selective_cleanup()


if __name__ == "__main__":
    main()
