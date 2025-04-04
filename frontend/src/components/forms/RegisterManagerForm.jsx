import React from "react";

const RegisterManagerForm = ({ style, token }) => {
    // Теперь ты можешь использовать token внутри компонента
    console.log("Полученный токен:", token);

    return (
        <div className={style.registrationForm}>
            <h2>Регистрация менеджера</h2>
            <p>Токен: {token}</p>
            {/* Тут остальные поля формы */}
        </div>
    );
};

export default RegisterManagerForm;
