# Use the official Nginx image from Docker Hub as a base image
FROM nginx

# Copy the custom Nginx configuration file into the container
COPY nginx.conf /etc/nginx/nginx.conf

# Copy the built Angular application into the Nginx web server's default folder
COPY ./dist/frontend-stjomme-nijst /usr/share/nginx/html

# Expose port 80 so the app can be accessed through the container
EXPOSE 80
