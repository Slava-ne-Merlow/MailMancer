import React from "react";
import { useNavigate } from "react-router-dom";
import userStore from "../../store/UserStore";
import { observer } from "mobx-react-lite";

const HomePage = observer(() => {
    const navigate = useNavigate();

    const handleLogout = () => {
        userStore.logout();
        navigate("/register");
    };

    return (
        <div>
            <h1>Добро пожаловать, {userStore.user?.userId || "Гость"}!</h1>
            <p>Вы успешно зарегистрировались.</p>
            <button onClick={handleLogout}>Выйти</button>
        </div>
    );
});

export default HomePage;
