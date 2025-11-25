FROM node:20-alpine

WORKDIR /app

COPY package*.json ./
RUN npm install --registry https://registry.npmjs.org/ --verbose

COPY . .

# Removed RUN npm run build - Next.js will run in development mode
# Removed VOLUME /app/.next - Docker will manage this internally

EXPOSE 3000

CMD ["npm", "run", "dev"] # Run in development mode for hot-reloading