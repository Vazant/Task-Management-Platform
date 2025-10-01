#!/usr/bin/env python3
import base64
from PIL import Image, ImageDraw

# Создаем простую иконку с логотипом
def create_icon(size):
    # Создаем изображение с синим фоном
    img = Image.new('RGBA', (size, size), (25, 118, 210, 255))
    draw = ImageDraw.Draw(img)
    
    # Добавляем белый прямоугольник в центре
    margin = size // 8
    draw.rectangle([margin, margin, size-margin, size-margin], fill=(255, 255, 255, 255))
    
    # Добавляем синие полоски (имитация списка задач)
    line_height = size // 16
    line_spacing = size // 8
    start_y = size // 4
    
    for i in range(4):
        y = start_y + i * line_spacing
        line_width = size - 2 * margin
        if i == 3:  # Последняя полоска короче
            line_width = line_width * 3 // 4
        draw.rectangle([margin, y, margin + line_width, y + line_height], fill=(25, 118, 210, 255))
    
    # Добавляем зеленую точку (галочка)
    dot_size = size // 16
    dot_x = size - margin - dot_size
    dot_y = start_y + line_height // 2
    draw.ellipse([dot_x, dot_y, dot_x + dot_size, dot_y + dot_size], fill=(76, 175, 80, 255))
    
    return img

# Создаем иконки разных размеров
sizes = [72, 96, 128, 144, 152, 192, 384, 512]

for size in sizes:
    icon = create_icon(size)
    icon.save(f'icon-{size}x{size}.png', 'PNG')
    print(f'Created icon-{size}x{size}.png')

print('All icons created successfully!')
