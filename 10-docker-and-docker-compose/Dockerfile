# Start from the alpine image
FROM alpine:3.18

# Set File Browser version as an argument
ARG FILEBROWSER_VERSION=2.25.0-r0

# Install the main filebrowser package from the testing repository
RUN apk add --no-cache --repository=https://dl-cdn.alpinelinux.org/alpine/edge/testing "filebrowser=$FILEBROWSER_VERSION"

# Install additional packages
RUN apk add --no-cache curl

# Set the working directory
WORKDIR /app

# Set volumes
VOLUME /app/config
VOLUME /app/data

# Set the exposed ports
EXPOSE 5001

# Set a health check
HEALTHCHECK --start-period=2s --interval=5s --timeout=3s \
    CMD curl -f http://localhost:5001/health || exit 1

# Override the entrypoint
ENTRYPOINT ["/usr/bin/filebrowser"]

# Set the default command
CMD ["--address", "0.0.0.0", "--port", "5001", "--database", "/app/config/database.db", "--root", "/app/data"]
