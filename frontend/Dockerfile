# Используем Node.js для сборки фронтенда
FROM node:18 AS build

# Устанавливаем рабочую директорию
WORKDIR /app

# Копируем package.json и package-lock.json и устанавливаем зависимости
COPY package.json package-lock.json ./
RUN npm install

# Копируем остальные файлы и собираем проект
COPY . .
RUN npm run build

# Используем Nginx для сервировки собранного фронтенда
FROM nginx:alpine
COPY --from=build /app/build /usr/share/nginx/html
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
