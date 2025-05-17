import { makeAutoObservable } from "mobx";

class UserStore {
    user = null;
    token = null;
    isAuth = false;

    constructor() {
        makeAutoObservable(this);
        this.loadUser();
    }

    setUser(role, login, name, token) {
        this.user = {role, login, name };
        this.token = token;
        this.isAuth = true;

        localStorage.setItem("user", JSON.stringify({role, login, name}));
        localStorage.setItem("token", token);
        localStorage.setItem("isAuth", "true");
    }

    loadUser() {
        const savedUser = localStorage.getItem("user");
        const savedToken = localStorage.getItem("token");
        const savedAuth = localStorage.getItem("isAuth");

        if (savedUser && savedToken && (savedAuth === "true")) {
            this.user = JSON.parse(savedUser);
            this.token = savedToken;
            this.isAuth = true;
        }

    }

    logout() {
        this.user = null;
        this.isAuth = false;

        localStorage.removeItem("user");
        localStorage.removeItem("isAuth");
    }
}

const userStore = new UserStore();
export default userStore;