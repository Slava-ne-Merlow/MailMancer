import React from "react";
import { Link } from "react-router-dom";
import style from "./CustomCheckbox.module.css";

export const CustomCheckbox = ({ checked, onChange, clearError }) => {
    const handleCheckboxClick = (e) => {
        onChange(e);
        clearError && clearError('agreedToTerms');
    };

    return (
        <label className={style.customCheckbox}>
            <input
                type="checkbox"
                checked={checked}
                onChange={handleCheckboxClick}
            />
            <span className={style.checkmark} />
            <span className={style.agreeLabel}>
                <span style={{ color: "#aaaaaa" }}>Please agree to the </span>
                <Link to="#" className={style.link}>
                    <span>terms of service</span>
                </Link>
                <span style={{ color: "#aaaaaa" }}>.</span>
            </span>
        </label>
    );
};
