import React, {useState} from "react";
import {motion} from "framer-motion";
import style from "./RegisterManagerForm.module.css";
import userStore  from "../../store/UserStore";
import {useNavigate} from "react-router-dom";

const RegisterManagerForm = ({ token }) => {
    const [step, setStep] = useState(1);
    const [formData, setFormData] = useState({
        fullName: "",

        login: "",

        password: "",

        checkPassword: "",

        agreedToTerms: false,
    });

    const [errors, setErrors] = useState({});
    const handleChange = (e) => {
        setFormData({...formData, [e.target.name]: e.target.value});
    };
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
            if (!formData.agreedToTerms) {
                newErrors.agreedToTerms = "You must agree";
                isValid = false;
            }

        }

        if (step === 2) {
            if (!formData.password) {
                newErrors.password = "Field is required!";
                isValid = false;
            } else if (formData.password.length < 6) {
                newErrors.password = "Password must be at least 6 characters";
                isValid = false;
            }
            if (!formData.checkPassword) {
                newErrors.checkPassword = "Field is required!";
                isValid = false;
            } else if (formData.checkPassword !== formData.password) {
                newErrors.checkPassword = "Passwords must match!";
                isValid = false;
            }
        }



        setErrors(newErrors);
        return isValid;
    };
    const navigate = useNavigate();

    const handleSubmit = async () => {

        const request = {
            name: formData.fullName,
            login: formData.login,
            password: formData.password,
            inviteToken: token,
        };

        try {
            const response = await fetch("http://localhost:8080/api/v1/manager/sign-up", {
                method: "POST",
                headers: {
                    "Content-Type": "application/json",
                },
                body: JSON.stringify(request),
            });

            const data = await response.json();

            if (!response.ok) {
                if (response.status === 409) {
                    if (data.message === `Логин ${formData.login} занят`) {
                        setErrors((prev) => ({...prev, login: "This login is already used"}));
                        prevStep()
                    }
                } else if (response.status === 404) {
                    if (data.message === "Приглашение недействительно") {
                        alert(data.message);
                    }
                } else if (response.status === 409) {
                    if (data.message === "Приглашение истекло") {
                        alert(data.message);
                    }
                } else {
                    alert("Ошибка регистрации: " + data.message + " " + data.status);
                }
                return false;
            }

            userStore.setUser(data.userId, data.companyId, data.token);
            return true;
        } catch (error) {
            console.error("Ошибка при отправке запроса:", error);
            alert("Ошибка сети. Попробуйте еще раз.");
            return false;
        }
    };



    const prevStep = () => setStep((prev) => prev - 1);
    const nextStep = () => {
        if (validateForm(formData)) {
            setStep((prev) => prev + 1);
        }
    }

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



        const success = await handleSubmit();
        if (success) {
            if (userStore.isAuth ){
                navigate("/home");
            }
        }
    };

    return (
        <div className={style.registrationForm}>
            <div className={style.div}>
                <div className={style.header}>Registration Form</div>
                <div className={`${step === 1 ? style.firstStep : style.secondStep }`}>
                    <div className={style.group}>
                        <div className={style.securityCheckup}>Security <br/> Checkup</div>
                        <div className={style.personalInformation}>Personal <br/> Information</div>

                    </div>
                </div>
                <motion.div
                    className={style.underline}
                    animate={{x: step === 1 ? 0 : 125 }}
                    transition={{type: "spring", stiffness: 100, damping: 10}}
                />

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

                        <div className={style.agreeInput1}>
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
                            top: 460
                        }}>
                            {errors.agreedToTerms && <p className={style.error}>{errors.agreedToTerms}</p>}
                        </div>

                        <button className={style.nextButton} onClick={nextStep}>
                            <div className={style.text}>Next</div>

                            <div className={style.text}>→</div>
                        </button>
                    </div>}
                {step === 2 &&
                    <div className={style.secondStep}>
                        <div className={style.firstInput}>
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

                        <div className={style.secondInput}>
                            <div className={style.inputLabel}>Confirm Password</div>
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

                        <button className={style.backButton} onClick={prevStep}>
                            <div className={style.text}>←</div>
                            <div className={style.text}>Back</div>
                        </button>

                        <button className={style.nextButton} onClick={handleDone}>
                            <div className={style.text}>Done</div>
                            <div className={style.text}>→</div>
                        </button>
                    </div>}


            </div>
        </div>
    );
};

export default RegisterManagerForm;
