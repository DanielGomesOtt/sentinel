# Stage 1: Build the React application
FROM node:22-alpine AS build

WORKDIR /app

COPY package*.json ./
RUN npm ci

COPY . .

ARG VITE_API_BASE_URL=""
ENV VITE_API_BASE_URL=$VITE_API_BASE_URL

RUN npm run build

# Stage 2: Serve the application with Nginx
FROM nginx:alpine

# Copy built static assets from previous stage
COPY --from=build /app/dist /usr/share/nginx/html

# Copy Nginx template for automatic envsubst of BACKEND_URL
COPY nginx.conf /etc/nginx/templates/default.conf.template

# Default backend URL pointing to host machine when running in Docker
ENV BACKEND_URL=http://host.docker.internal:8080
ENV NGINX_ENVSUBST_FILTER=BACKEND_URL

EXPOSE 80

CMD ["nginx", "-g", "daemon off;"]
