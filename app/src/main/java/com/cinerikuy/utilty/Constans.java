package com.cinerikuy.utilty;

public class Constans {
    public static final String BASE_URL = "https://f887-179-6-168-33.ngrok-free.app/";
    //public static final String BASE_URL = "http://192.168.1.41:8080/";
    public static final String BACKEND_CUSTOMER = BASE_URL + "customers/";
    public static final String BACKEND_MOVIE = BASE_URL+"movies/";
    public static final String BACKEND_CINEMA = BASE_URL;
    public static final String BACKEND_PRODUCTS = BASE_URL;
    public static final String BACKEND_TRANSACTION = BASE_URL+"transactions/";
    public static final String MESSAGE_OBLIGATORIO = "Campo obligatorio";
    public static final String MESSAGE_ALL_OBLIGATORIO = "Los campos son obligatorios";
    public static final String REGEX_USERNAME = "^(?=.*[a-zA-Z])(?=.*[0-9]).+$";
    public static final String REGEX_PASSWORD = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).+$";
    public static final String REGEX_NAMES = "^[a-zA-Z ]*$";
    public static final String REGEX_CELLPHONE = "^9.*$";
    public static final String REGEX_DNI = "^[0-9]+$";
    public static final String MESSAGE_USERNAME_INVALID = "El username debe ser mínimo de 2 y máximo 8 caracteres, solo letras y números.";
    public static final String MESSAGE_PASSWORD_INVALID = "El password debe ser mínimo de 4 y máximo de 8 caracteres, y tener como mínimo: 1 letra minúscula, 1 letra mayúscula, y 1 dígito.";
    public static final String MESSAGE_NAMES_INVALID = "Solo letras";
    public static final String MESSAGE_CELLPHONE_INVALID = "Número de telefono invalido";
    public static final String MESSAGE_DNI_INVALID = "Número de DNI invalido";
    public static final String URL_MANUAL_USER = "https://64843639b6fff33b312efdc4--statuesque-parfait-f98f99.netlify.app/";
}
