FROM amazoncorretto:21-alpine-jdk

RUN apk add --no-cache \
    tesseract-ocr \
    tesseract-ocr-data-rus \
    tesseract-ocr-data-eng \
    tesseract-ocr-dev \
    leptonica-dev \
    libwebp-dev \
    libpng-dev \
    libjpeg-turbo-dev \
    tiff-dev \
    zlib-dev

WORKDIR /app
COPY target/*.jar app.jar
COPY pdfs/ /app/pdfs/

ENTRYPOINT ["java", "-jar", "app.jar"]