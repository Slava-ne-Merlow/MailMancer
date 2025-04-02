import { makeAutoObservable } from "mobx";

class UserStore {
    user = null; // Данные пользователя (userId, companyId)
    token = null; // Токен авторизации
    isAuth = false; // Авторизован ли пользователь

    constructor() {
        makeAutoObservable(this);
        this.loadUser();
    }

    // Устанавливаем пользователя, токен и сохраняем в localStorage
    setUser(userId, companyId, token) {
        this.user = { userId, companyId };
        this.token = token;
        this.isAuth = true;

        localStorage.setItem("user", JSON.stringify({ userId, companyId }));
        localStorage.setItem("token", token);
        localStorage.setItem("isAuth", "true");
    }

    // Загружаем пользователя из localStorage при старте
    loadUser() {
        const savedUser = localStorage.getItem("user");
        const savedToken = localStorage.getItem("token");
        const savedAuth = localStorage.getItem("isAuth");

        if (savedUser && savedToken && savedAuth === "true") {
            this.user = JSON.parse(savedUser);
            this.token = savedToken;
            this.isAuth = true;
        }
    }

    // Выход из системы
    logout() {
        this.user = null;
        this.token = null;
        this.isAuth = false;

        localStorage.removeItem("user");
        localStorage.removeItem("token");
        localStorage.removeItem("isAuth");
    }
}

export default new UserStore();
