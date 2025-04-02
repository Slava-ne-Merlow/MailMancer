import React, { useState } from "react";
import { motion } from "framer-motion";
import style from "./RegisterFrom.module.css";
import userStore from "../../store/UserStore";
import {useNavigate} from "react-router-dom";

const RegisterPage = () => {
    const [step, setStep] = useState(1);
    const [formData, setFormData] = useState({
        fullName: "",
        login: "",
        password: "",

        companyName: "",
        companyEmail: "",
        companyPassword: "",

        checkPassword: "",
        checkCompanyEmail: "",
        checkCompanyPassword : "",

        agreedToTerms: false,
    });
    const [errors, setErrors] = useState({});

    const navigate = useNavigate();

    const handleSubmit = async () => {

        const request = {
            headLogin: formData.login,
            headName: formData.fullName,
            headPassword: formData.password,

            companyName: formData.companyName,
            email: formData.companyEmail,
            emailPassword: formData.companyPassword,
        };
        console.log(request);

        try {
            const response = await fetch("http://localhost:8080/api/v1/head/sign-up", {
                method: "POST",
                headers: {
                    "Content-Type": "application/json",
                },
                body: JSON.stringify(request),
            });

            const data = await response.json();
            console.log("response");
            console.log(response);
            console.log(response.status);
            console.log("data");
            console.log(data);

            if (!response.ok) {
                if (response.status === 409) {
                    if (data.message === `Логин ${formData.login} занят`){
                        setErrors((prev) => ({ ...prev, login: "This login is already used" }));
                        prevStep()
                        prevStep()
                    }
                    if (data.message === `Почта ${formData.companyEmail} занята`){
                        setErrors((prev) => ({ ...prev, companyEmail: "This mail is already used" }));
                        prevStep()
                    }
                } else {
                    alert("Ошибка регистрации: " + data.message + " " + data.status);
                }
                return;
            }

            // Если успешно — сохраняем пользователя и переходим на главную страницу
            userStore.setUser(data.userId, data.companyId, data.token);
            navigate("/home");
            return true;
        } catch (error) {
            console.error("Ошибка при отправке запроса:", error);
            alert("Ошибка сети. Попробуйте еще раз.");
        }
    };


    const handleChange = (e) => {
        setFormData({ ...formData, [e.target.name]: e.target.value });
    };

    const handleCheckboxChange = (e) => {
        setFormData({ ...formData, agreedToTerms: e.target.checked });
    };

    const nextStep = () => {
        if (validateForm(formData)) {
            setStep((prev) => prev + 1);
        }
    }
    const prevStep = () => setStep((prev) => prev - 1);

    const validateForm = () => {
        const newErrors = {};
        let isValid = true;
        if (step === 1) {
            if (!formData.fullName) {
                newErrors.fullName = "Field is required!";
                isValid = false;
            }
            if (!formData.login) {
                newErrors.login = "Field is required!";
                isValid = false;
            } else if (errors.login) {
                newErrors.login = errors.login;
                isValid = false;
            }
            if (!formData.password) {
                newErrors.password = "Field is required!";
                isValid = false;
            } else if (formData.password.length < 6) {
                newErrors.password = "Password must be at least 6 characters";
                isValid = false;
            }
            if (!formData.agreedToTerms) {
                newErrors.agreedToTerms = "You must agree";
                isValid = false;
            }

        }

        if (step === 2) {
            if (!formData.companyName) {
                newErrors.companyName = "Field is required!";
                isValid = false;
            }
            if (!formData.companyEmail) {
                newErrors.companyEmail = "Field is required!";
                isValid = false;
            } else if (!/^(([^<>()[\]\\.,;:\s@"]+(\.[^<>()[\]\\.,;:\s@"]+)*)|(".+"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/.test(formData.companyEmail)){
                newErrors.companyEmail = "Enter email in format: email@example.com!";
                isValid = false;
            } else if (errors.companyEmail) {
                newErrors.companyEmail = errors.companyEmail;
                isValid = false;
            }
            if (!formData.companyPassword) {
                newErrors.companyPassword = "Field is required!";
                isValid = false;
            } else if (formData.companyPassword.length < 6) {
                newErrors.companyPassword = "Password must be at least 6 characters";
                isValid = false;
            }
        }

        if (step === 3) {

            if (!formData.checkPassword) {
                newErrors.checkPassword = "Field is required!";
                isValid = false;
            } else if (formData.checkPassword !== formData.password) {
                newErrors.checkPassword = "Passwords must match!";
                isValid = false;
            }

            if (!formData.checkCompanyEmail) {
                newErrors.checkCompanyEmail = "Field is required!";
                isValid = false;
            } else if (formData.companyEmail !== formData.checkCompanyEmail) {
                newErrors.checkCompanyEmail = "Emails must match!";
                isValid = false;
            }

            if (!formData.checkCompanyPassword) {
                newErrors.checkCompanyPassword = "Field is required!";
                isValid = false;
            } else if (formData.companyPassword !== formData.checkCompanyPassword) {
                newErrors.checkCompanyPassword = "Passwords must match!";
                isValid = false;
            }

        }

        setErrors(newErrors);
        return isValid;
    };
    const clearError = (fieldName) => {
        setErrors(prev => ({ ...prev, [fieldName]: "" }));
    };
    const handleDone = () => {
        const isFormValid = validateForm();

        if (!isFormValid) {
            return;
        }


        console.log("Form is valid, send data to server.");

        handleSubmit()
    };

    return (
        <div className={style.registrationForm}>
            <div className={style.div}>
                <div className={style.header}>Registration Form</div>
                <div className={`${step === 1 ? style.firstStep : step === 2 ? style.secondStep : style.thirdStep}`}>
                    <div className={style.group}>
                        <div className={style.companyInformation}>Company <br/> Information</div>
                        <div className={style.personalInformation}>Personal <br/> Information</div>
                        <div className={style.securityCheckup}>Security <br/> Checkup</div>

                        <motion.div
                            className={style.underline}
                            animate={{x: step === 1 ? 0 : step === 2 ? 125 : 251}} // Меняет позицию
                            transition={{type: "spring", stiffness: 100, damping: 10}} // Плавное движение
                        />
                    </div>
                </div>

                {step === 1 &&
                    <div className={style.firstStep}>

                        <div className={style.firstInput}>
                            <div className={style.inputLabel}>Full Name</div>
                            <input
                                className={style.inputField}
                                name="fullName"
                                value={formData.fullName}
                                onChange={handleChange}
                                onFocus={() => clearError('fullName')}
                            />
                            {errors.fullName && <p className={style.error}>{errors.fullName}</p>}
                        </div>

                        <div className={style.secondInput}>
                            <div className={style.inputLabel}>Login</div>
                            <input
                                className={style.inputField}
                                name="login"
                                value={formData.login}
                                onChange={handleChange}
                                onFocus={() => clearError('login')}
                            />
                            {errors.login && <p className={style.error}>{errors.login}</p>}
                        </div>

                        <div className={style.thirdInput}>
                            <div className={style.inputLabel}>Password</div>
                            <input
                                type="password"
                                className={style.inputField}
                                name="password"
                                value={formData.password}
                                onChange={handleChange}
                                onFocus={() => clearError('password')}

                            />
                            {errors.password && <p className={style.error}>{errors.password}</p>}
                        </div>

                        <div className={style.agreeInput}>
                            <label className={style.customCheckbox}>
                                <input
                                    type="checkbox"
                                    checked={formData.agreedToTerms}
                                    onChange={(e) => {
                                        handleCheckboxChange(e);
                                        clearError('agreedToTerms');
                                    }}
                                />
                                <span className={style.checkmark}></span>
                            </label>

                            <p className={style.agreeLabel}>
                                <span style={{color: "#aaaaaa"}}>Please agree to the </span>
                                <span style={{color: "#000000"}}>terms of service</span>
                                <span style={{color: "#aaaaaa"}}>.</span>
                            </p>
                        </div>
                        <div style={{
                            display: "flex",
                            justifyContent: "space-between",
                            left: 524,
                            position: "absolute",
                            top: 530
                        }}>
                        {errors.agreedToTerms && <p className={style.error}>{errors.agreedToTerms}</p>}
                    </div>

                        <button className={style.nextButton} onClick={nextStep}>
                            <div className={style.text}>Next</div>
                            <div className={style.text}>→</div>
                        </button>
                    </div>
                }

                {step === 2 &&
                    <div className={style.secondStep}>
                        <div className={style.firstInput}>
                            <div className={style.inputLabel}>Company Name</div>
                            <input
                                className={style.inputField}
                                name="companyName"
                                value={formData.companyName}
                                onChange={handleChange}
                                onFocus={() => clearError('companyName')}
                            />
                            {errors.companyName && <p className={style.error}>{errors.companyName}</p>}
                        </div>

                        <div className={style.secondInput}>
                            <div className={style.inputLabel}>Company Email</div>
                            <input
                                type="email"
                                className={style.inputField}
                                name="companyEmail"
                                value={formData.companyEmail}
                                onChange={handleChange}
                                onFocus={() => clearError('companyEmail')}

                            />
                            {errors.companyEmail && <p className={style.error}>{errors.companyEmail}</p>}
                        </div>

                        <div className={style.thirdInput}>
                            <div className={style.inputLabel}>Company Password</div>
                            <input
                                type="password"
                                className={style.inputField}
                                name="companyPassword"
                                value={formData.companyPassword}
                                onChange={handleChange}
                                onFocus={() => clearError('companyPassword')}

                            />
                            {errors.companyPassword && <p className={style.error}>{errors.companyPassword}</p>}

                        </div>

                        <button className={style.backButton} onClick={prevStep}>
                            <div className={style.text}>←</div>
                            <div className={style.text}>Back</div>
                        </button>

                        <button className={style.nextButton} onClick={nextStep}>
                            <div className={style.text}>Next</div>
                            <div className={style.text}>→</div>
                        </button>
                    </div>
                }

                {step === 3 &&
                    <div className={style.thirdStep}>
                        <div className={style.firstInput}>
                            <div className={style.inputLabel}>Your Password</div>
                            <input
                                type="password"
                                className={style.inputField}
                                name="checkPassword"
                                value={formData.checkPassword}
                                onChange={handleChange}
                                onFocus={() => clearError('checkPassword')}

                            />
                            {errors.checkPassword && <p className={style.error}>{errors.checkPassword}</p>}
                        </div>

                        <div className={style.secondInput}>
                            <div className={style.inputLabel}>Company Email</div>
                            <input
                                type="email"
                                className={style.inputField}
                                name="checkCompanyEmail"
                                value={formData.checkCompanyEmail}
                                onChange={handleChange}
                                onFocus={() => clearError('checkCompanyEmail')}

                            />
                            {errors.checkCompanyEmail && <p className={style.error}>{errors.checkCompanyEmail}</p>}
                        </div>

                        <div className={style.thirdInput}>
                            <div className={style.inputLabel}>Company Email Password</div>
                            <input
                                type={"password"}
                                className={style.inputField}
                                name="checkCompanyPassword"
                                value={formData.checkCompanyPassword}
                                onChange={handleChange}
                                onFocus={() => clearError('checkCompanyPassword')}

                            />
                            {errors.checkCompanyPassword && <p className={style.error}>{errors.checkCompanyPassword}</p>}
                        </div>

                        <button className={style.backButton} onClick={prevStep}>
                            <div className={style.text}>←</div>
                            <div className={style.text}>Back</div>
                        </button>

                        <button className={style.nextButton} onClick={handleDone}>
                                <div className={style.text}>Done</div>
                                <div className={style.text}>→</div>
                            </button>
                        </div>
                }
            </div>
        </div>
        );
};

export default RegisterPage;
