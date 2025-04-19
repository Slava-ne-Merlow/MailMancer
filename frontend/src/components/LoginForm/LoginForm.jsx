import React, {useState} from "react";
import style from "./LoginForm.module.css";
import {Link, useNavigate} from "react-router-dom";
import userStore from "../../store/UserStore";

const LoginForm = () => {
    const navigate = useNavigate();
    const [formData, setFormData] = useState({
        login: "",

        password: "",

        agreedToTerms: false,
    });

    const [errors, setErrors] = useState({});
    const handleChange = (e) => {
        setFormData({...formData, [e.target.name]: e.target.value});
    };
    const validateForm = () => {
        const newErrors = {};
        let isValid;

        if (!formData.login) newErrors.login = "Field is required!";
        if (!formData.password) newErrors.password = "Field is required!";
        if (!formData.agreedToTerms) newErrors.agreedToTerms = "You must agree";

        isValid = Object.keys(newErrors).length === 0;

        setErrors(newErrors);
        return isValid;
    };


    const handleSubmit = async () => {

        const request = {
            login: formData.login,
            password: formData.password,
        };

        try {
            const response = await fetch("http://localhost:8080/api/v1/sign-in", {
                method: "POST",
                headers: {
                    "Content-Type": "application/json",
                },
                body: JSON.stringify(request),
            });

            const data = await response.json();
            if (!response.ok) {
                if (response.status === 404) {
                    if (data.message === `Логин ${request.login} занят`) {
                        setErrors((prev) => ({...prev, login: "This login does not exist"}));
                    }
                } else if (response.status === 401) {
                    if (data.message === "Неверный логин или пароль") {
                        setErrors((prev) => ({...prev, login: "Invalid login or password"}));
                        setErrors((prev) => ({...prev, password: "Invalid login or password"}));
                    }
                } else {
                    alert("Ошибка регистрации: " + data.message + " " + data.status);
                }
                return null;
            }

            return data;
        } catch (error) {
            console.error("Ошибка при отправке запроса:", error);
            alert("Ошибка сети. Попробуйте еще раз.");
            return null;
        }
    };

    const handleCheckboxChange = (e) => {
        setFormData({...formData, agreedToTerms: e.target.checked});
    };
    const clearError = (fieldName) => {
        setErrors(prev => ({...prev, [fieldName]: ""}));
    };
    const handleDone = async () => {
        const isFormValid = validateForm();

        if (!isFormValid) {
            return;
        }


        const data = await handleSubmit();
        if (data) {
            userStore.setUser(data.userId, data.companyId, data.token, data.role, data.login, data.name);
            navigate("/mailings");

        }
    };


    return (
        <div className={style.loginForm}>
            <div className={style.div}>
                <div className={style.header}>Login to Account</div>

                <div className={style.login}>
                    <div className={style.loginLabel}>Login</div>

                    <input
                        className={style.input}
                        name="login"
                        value={formData.login}
                        onChange={handleChange}
                        onFocus={() => clearError('login')}
                    />

                    {errors.login && <p className={style.error}>{errors.login}</p>}

                </div>

                <div className={style.password}>
                    <div className={style.passwordLabel}>Password</div>

                    <input
                        type="password"
                        className={style.input}
                        name="password"
                        value={formData.password}
                        onChange={handleChange}
                        onFocus={() => clearError('password')}
                    />
                    {errors.password && <p className={style.error}>{errors.password}</p>}

                </div>


                <div className={style.agree}>
                    <label className={style.customCheckbox}>
                        <input
                            type="checkbox"
                            checked={formData.agreedToTerms}
                            onChange={(e) => {
                                handleCheckboxChange(e);
                                clearError('agreedToTerms');
                            }}
                        />

                        <span className={style.checkmark}/>
                    </label>

                    <p className={style.agreeLabel}>
                        <span style={{color: "#aaaaaa"}}>Please agree to the </span>
                        <Link to="#" className={style.link}>
                            <span>terms of service</span>
                        </Link>
                        <span style={{color: "#aaaaaa"}}>.</span>
                    </p>
                </div>
                <div style={{
                    display: "flex",
                    justifyContent: "space-between",
                    position: "absolute",
                    marginTop: "3px",
                    top: 270,
                    left: 125
                }}>
                    {errors.agreedToTerms && <p className={style.error}>{errors.agreedToTerms}</p>}
                </div>


                <div className={style.nextButton} onClick={handleDone}>
                    <div className={style.text}>Done</div>

                    <div className={style.text}>→</div>
                </div>

                <div className={style.toRegister}>
                    <p className={style.text}>
                        <span style={{color: "#aaaaaa"}}>Don’t have account? </span>
                        <Link to="/register" className={style.link} style={{fontFamily: "Montserrat Alternates"}}>
                            <span>Create Account</span>
                        </Link>
                    </p>
                </div>
            </div>
        </div>
    );
};

export default LoginForm;