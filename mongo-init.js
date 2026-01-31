// Script de inicialización para MongoDB
// Se ejecuta automáticamente al crear el contenedor por primera vez

// Cambiar a la base de datos de la aplicación
db = db.getSiblingDB('avoris-demo-db');

// Crear usuario específico para la aplicación
db.createUser({
  user: 'avoris-user',
  pwd: 'avoris-password',
  roles: [
    {
      role: 'readWrite',
      db: 'avoris-demo-db'
    }
  ]
});

// Crear colecciones iniciales con índices básicos
db.createCollection('searches');

// Crear índices para mejorar rendimiento
db.searches.createIndex({ "searchId": 1 }, { unique: true });

print('Base de datos avoris-demo-db inicializada correctamente con usuario avoris-user');
print('Coleccion creada:  search');
print('Índices creados search.searchId');
