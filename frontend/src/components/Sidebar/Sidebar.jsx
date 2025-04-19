import React from "react";
import SidebarItem from "../SidebarItem/SidebarItem";

import mailIcon from "../../assets/icons/mailings.svg";
import teamIcon from "../../assets/icons/team.svg";
import plusIcon from "../../assets/icons/plus.svg";
import carriers from "../../assets/icons/carriers.svg";
import styles from "./Sidebar.module.css";

const Sidebar = ({selected}) => {
    return (
        <div className={styles.sidebar}>
            <SidebarItem icon={mailIcon} label="Mailings" selected={selected === "mailings"} href={"/mailings"}/>
            <SidebarItem icon={teamIcon} label="Team" selected={selected === "team"} href={"/teams"}/>
            <SidebarItem icon={carriers} label="Carriers" selected={selected === "carriers"} href={"/carriers"}/>
            <SidebarItem icon={plusIcon} label="Create Mailing" selected={selected === "create"} href={"/create"}/>
        </div>
    );
};

export default Sidebar;