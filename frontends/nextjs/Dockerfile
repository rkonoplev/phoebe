FROM node:20-alpine

WORKDIR /app

COPY package*.json ./
RUN npm install --registry https://registry.npmjs.org/ --verbose
# Removed VOLUME /app/node_modules - Docker will manage this internally

COPY . .

RUN npm run build
# Removed VOLUME /app/.next - Docker will manage this internally

EXPOSE 3000

CMD ["npm", "start"]