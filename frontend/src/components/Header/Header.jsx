import React from "react";

import styles from "./Header.module.css";
import burgerMenu from "../../assets/icons/burger-menu.svg";
import search from "../../assets/icons/search.svg";
import NotificationButton from "../NotificationButton/NotificationButton";
import avatar from "../../assets/icons/avatar.svg";
import more from "../../assets/icons/more.svg";
import userStore from "../../store/UserStore";

const Header = () => {

    return (
        <div className={styles.header}>
            <div className={styles.leftSection}>
                <div className={styles.logo}>
                    <p className={styles.text}>
                        <span style={{color: "#000000"}}>Mail</span>

                        <span style={{color: "#aaaaaa"}}>Mancer</span>
                    </p>
                </div>
                <img className={styles.burgerMenu} alt="Burger menu" src={burgerMenu}/>

                <input
                    className={styles.search}
                    placeholder="Search"
                    type="text"
                    style={{
                        backgroundImage: `url(${search})`,
                        backgroundRepeat: "no-repeat",
                        backgroundPosition: "10px center",
                        paddingLeft: "40px",
                        paddingRight: "40px",
                    }}
                />

            </div>
            <div className={styles.rightSection}>
                <NotificationButton count={6}/>


                <div className={styles.profile}>
                    <img className={styles.avatar} alt="Avatar" src={avatar}/>

                    <div className={styles.profileInfo}>
                        <div className={styles.name}>{userStore.user?.name || "—"}</div>

                        <div className={styles.role}>{userStore.user?.role || "—"}</div>

                    </div>
                    <img alt="More" src={more}/>

                </div>
            </div>

        </div>

    );
};

export default Header;