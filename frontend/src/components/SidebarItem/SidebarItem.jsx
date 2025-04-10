import React from "react";
import styles from "./SidebarItem.module.css";
import classNames from "classnames";
import {Link} from "react-router-dom";

const SidebarItem = ({ icon, label, selected, href}) => {
    return (
        <Link to={href} href={href} className={styles.row}>
            <div className={styles.tail} style={{backgroundColor: `${selected ? "#000000" : "#ffffff"}`}}/>
            <div className={classNames(styles.item, {[styles.selected]: selected})}>
                <img src={icon} alt={label}/>
                <span className={styles.label}>{label}</span>
            </div>
        </Link>
    );
};

export default SidebarItem;
