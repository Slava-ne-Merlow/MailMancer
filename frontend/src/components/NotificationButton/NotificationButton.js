import React from "react";
import bell from "../../assets/icons/bell.svg";
import styles from "./NotificationButton.module.css";

const NotificationButton = ({count}) => {
    const getDisplayCount = () => {
        if (count > 9) return "9+";
        return count.toString();
    };

    const getFontSize = () => {
        if (count > 9) return "8px";
        return "10px";
    };

    return (
        <div className={styles.notifications}>
            <img src={bell} alt="Notifications"/>
            {count > 0 && (
                <div
                    className={styles.badge}
                    style={{fontSize: getFontSize()}}
                >
                    {getDisplayCount()}
                </div>
            )}
        </div>
    );
};

export default NotificationButton;
