const fs = require('fs');

// Простая PNG иконка 1x1 пиксель (минимальный PNG)
const createSimplePNG = (size) => {
  // Создаем простой PNG с помощью canvas API или создаем минимальный PNG
  const canvas = `
<svg width="${size}" height="${size}" xmlns="http://www.w3.org/2000/svg">
  <rect width="${size}" height="${size}" fill="#1976d2"/>
  <rect x="${size/8}" y="${size/8}" width="${size*3/4}" height="${size*3/4}" fill="white" rx="${size/16}"/>
  <rect x="${size/4}" y="${size/4}" width="${size/2}" height="${size/16}" fill="#1976d2"/>
  <rect x="${size/4}" y="${size/4 + size/8}" width="${size*3/8}" height="${size/16}" fill="#1976d2"/>
  <rect x="${size/4}" y="${size/4 + size/4}" width="${size*5/16}" height="${size/16}" fill="#1976d2"/>
  <circle cx="${size*3/4}" cy="${size*3/4}" r="${size/16}" fill="#4caf50"/>
</svg>`;
  
  return canvas;
};

const sizes = [72, 96, 128, 144, 152, 192, 384, 512];

sizes.forEach(size => {
  const svg = createSimplePNG(size);
  fs.writeFileSync(`icon-${size}x${size}.png`, svg);
  console.log(`Created icon-${size}x${size}.png`);
});

console.log('All icons created successfully!');
