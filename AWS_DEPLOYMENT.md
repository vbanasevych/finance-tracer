# AWS EC2 Deployment Guide

Цей документ описує процес розгортання застосунку "Finance Tracker" на хмарній платформі Amazon Web Services (AWS) за допомогою Docker та Docker Compose.

## 1. Підготовка інфраструктури (AWS EC2)
1. Створено віртуальний сервер (EC2 Instance) з ОС **Ubuntu 24.04 LTS**.
2. Налаштовано **Security Group**:
    - `SSH` (порт 22) — для доступу до термінала сервера.
    - `Custom TCP` (порт 8080) — для доступу до веб-додатку з будь-якого IP (`0.0.0.0/0`).

## 2. Налаштування сервера
Підключення до сервера виконано через SSH. На сервері встановлено необхідне програмне забезпечення:
```bash
sudo apt update
sudo apt install docker.io docker-compose -y