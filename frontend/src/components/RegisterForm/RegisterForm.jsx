import React, {useState} from "react";
import {Link, useNavigate, useSearchParams} from "react-router-dom";
import userStore from "../../store/UserStore";
import {motion} from "framer-motion";
import style from "./RegisterFrom.module.css";
import {CustomCheckbox} from "../CustomCheckbox/CustomCheckbox";


const RegisterForm = () => {
    const [searchParams] = useSearchParams();
    const token = searchParams.get("token");
    let url
    if (token) {
        url = "http://localhost:8080/api/v1/manager/sign-up";
    } else {
        url = "http://localhost:8080/api/v1/head/sign-up"
    }

    const [step, setStep] = useState(1);
    const [formData, setFormData] = useState({
        fullName: "",
        login: "",

        email: "",
        confirmEmail: "",

        password: "",
        confirmPassword: "",

        agreedToTerms: false,
    });
    const [errors, setErrors] = useState({});

    const navigate = useNavigate();



    const handleSubmit = async () => {

        const request = {
            name: formData.fullName,
            login: formData.login,
            password: formData.password,
            email: formData.email
        };

        if (token) {
            request.token = token;
        }

        try {
            const response = await fetch(url, {
                method: "POST",
                headers: {
                    "Content-Type": "application/json",
                },
                body: JSON.stringify(request),
            });

            const data = await response.json();

            if (!response.ok) {
                if (response.status === 409) {
                    console.log(data.message);
                    if (data.message === `Логин ${request.login} занят`) {
                        setErrors((prev) => ({...prev, login: "This login is already used"}));
                        prevStep()
                        prevStep()
                    }
                    if (data.message === `Почта ${request.email} занята`) {
                        setErrors((prev) => ({...prev, email: "This mail is already used"}));
                        prevStep()
                    }
                } else {
                    alert("Ошибка регистрации: " + data.message + " " + data.status);
                }
                return false;
            }

            userStore.setUser(data.role, data.login, data.name, data.token);
            return true;
        } catch (error) {
            console.error("Ошибка при отправке запроса:", error);
            alert("Ошибка сети. Попробуйте еще раз.");
            return false
        }
    };


    const handleChange = (e) => {
        setFormData({...formData, [e.target.name]: e.target.value});
    };

    const handleCheckboxChange = (e) => {
        setFormData({...formData, agreedToTerms: e.target.checked});
    };

    const nextStep = () => {
        if (validateForm(formData)) {
            setStep((prev) => prev + 1);
        }
    }
    const prevStep = () => setStep((prev) => prev - 1);

    const validateForm = () => {
        const newErrors = {};
        let isValid;

        if (step === 1) {
            if (!formData.fullName) newErrors.fullName = "Field is required!";

            if (!formData.login) newErrors.login = "Field is required!";
            else if (errors.login) newErrors.login = errors.login;

        }
        if (step === 2) {
            if (!formData.email) newErrors.email = "Field is required!";
            else if (!/^(([^<>()[\]\\.,;:\s@"]+(\.[^<>()[\]\\.,;:\s@"]+)*)|(".+"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/.test(formData.email)) newErrors.email = "Enter email in format: email@example.com!";
            else if (errors.email) newErrors.email = errors.email;

            if (!formData.confirmEmail) newErrors.confirmEmail = "Field is required!";
            else if (!/^(([^<>()[\]\\.,;:\s@"]+(\.[^<>()[\]\\.,;:\s@"]+)*)|(".+"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/.test(formData.confirmEmail)) newErrors.confirmEmail = "Enter email in format: email@example.com!";
            else if (formData.confirmEmail !== formData.email) newErrors.confirmEmail = "Emails must match!";
            else if (errors.confirmEmail) newErrors.confirmEmail = errors.confirmEmail;
        }
        if (step === 3) {
            if (!formData.password) newErrors.password = "Field is required!";
            else if (formData.password.length < 6) newErrors.password = "Password must be at least 6 characters";

            if (!formData.confirmPassword) newErrors.confirmPassword = "Field is required!";
            else if (formData.confirmPassword !== formData.password) newErrors.confirmPassword = "Passwords must match!";
        }
        if (!formData.agreedToTerms) newErrors.agreedToTerms = "You must agree";

        isValid = Object.keys(newErrors).length === 0;

        setErrors(newErrors);
        return isValid;
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
            if (userStore.isAuth) {
                navigate("/mailings");
            }
        }
    };

    return (
        <div className={style.registrationForm}>
            <div className={style.card}>
                <div className={style.header}>Create an Account</div>

                <div className={`${step === 1 ? style.firstStep : step === 2 ? style.secondStep : style.thirdStep}`}>
                    <div className={style.group}>
                        <div className={style.personalInformation}>Personal <br/> Information</div>
                        <div className={style.companyInformation}>Company <br/> Information</div>
                        <div className={style.securityCheckup}>Security <br/> Checkup</div>

                        <motion.div
                            className={style.underline}
                            animate={{x: step === 1 ? -5 : step === 2 ? 130 : 255}}
                            transition={{type: "spring", stiffness: 100, damping: 10}}
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


                        <div className={style.agree}>

                            <CustomCheckbox
                                checked={formData.agreedToTerms}
                                onChange={handleCheckboxChange}
                                clearError={clearError}
                            />

                            {errors.agreedToTerms && <p className={style.error}>{errors.agreedToTerms}</p>}

                        </div>


                        <div className={style.btn} onClick={nextStep}>
                            <div className={style.text}>Next</div>
                            <div className={style.text}>→</div>
                        </div>


                    </div>
                }

                {step === 2 &&
                    <div className={style.secondStep}>
                        <div className={style.firstInput}>
                            <div className={style.inputLabel}>Email</div>
                            <input
                                className={style.inputField}
                                name="email"
                                value={formData.email}
                                onChange={handleChange}
                                onFocus={() => clearError('email')}
                            />
                            {errors.email && <p className={style.error}>{errors.email}</p>}
                        </div>

                        <div className={style.secondInput}>
                            <div className={style.inputLabel}>Confirm Email</div>
                            <input
                                type="email"
                                className={style.inputField}
                                name="confirmEmail"
                                value={formData.confirmEmail}
                                onChange={handleChange}
                                onFocus={() => clearError('confirmEmail')}

                            />
                            {errors.confirmEmail && <p className={style.error}>{errors.confirmEmail}</p>}
                        </div>

                        <div className={style.agree}>

                            <CustomCheckbox
                                checked={formData.agreedToTerms}
                                onChange={handleCheckboxChange}
                                clearError={clearError}
                            />

                            {errors.agreedToTerms && <p className={style.error}>{errors.agreedToTerms}</p>}

                        </div>

                        <div className={style.navigation}>
                            <div className={style.backButton} onClick={prevStep}>
                                <div className={style.text}>←</div>
                                <div className={style.text}>Back</div>
                            </div>

                            <div className={style.nextButton} onClick={nextStep}>
                                <div className={style.text}>Next</div>
                                <div className={style.text}>→</div>
                            </div>
                        </div>
                    </div>
                }

                {step === 3 &&
                    <div className={style.thirdStep}>
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
                                name="confirmPassword"
                                value={formData.confirmPassword}
                                onChange={handleChange}
                                onFocus={() => clearError('confirmPassword')}

                            />
                            {errors.confirmPassword && <p className={style.error}>{errors.confirmPassword}</p>}
                        </div>

                        <div className={style.agree}>

                            <CustomCheckbox
                                checked={formData.agreedToTerms}
                                onChange={handleCheckboxChange}
                                clearError={clearError}
                            />

                            {errors.agreedToTerms && <p className={style.error}>{errors.agreedToTerms}</p>}

                        </div>

                        <div className={style.navigation}>

                            <div className={style.backButton} onClick={prevStep}>
                                <div className={style.text}>←</div>
                                <div className={style.text}>Back</div>
                            </div>

                            <div className={style.nextButton} onClick={handleDone}>
                                <div className={style.text}>Done</div>
                                <div className={style.text}>→</div>
                            </div>
                        </div>
                    </div>
                }
                <div className={style.redirect}>
                    <p className={style.redirectLLabel}>
                        <span style={{color: "#aaaaaa"}}>Already have an account? </span>
                        <Link to="/login" className={style.link} style={{fontFamily: "Montserrat Alternates"}}>
                            <span>Login</span>
                        </Link>
                    </p>
                </div>
            </div>
        </div>
    );
};

export default RegisterForm;
