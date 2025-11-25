FROM node:20-alpine

WORKDIR /app

COPY package*.json ./
RUN npm install --registry https://registry.npmjs.org/ --verbose
VOLUME /app/node_modules # Declare node_modules as a volume for caching

COPY . .

RUN npm run build
VOLUME /app/.next # Declare .next as a volume for caching

EXPOSE 3000

CMD ["npm", "start"]