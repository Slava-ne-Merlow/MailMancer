import Header from "../../../components/Header/Header";
import Sidebar from "../../../components/Sidebar/Sidebar";
import styles from "./MailingsPage.module.css";
import React from "react";
import OrderCard from "../../../components/OrderCard/OrderCard";

const MailingsPage = () => {
    return (
        <div className="main-page">
            <Header/>

            <Sidebar selected="mailings"/>
            <div className="main-content">
                <div className="content-header">
                    <div className="text">Mailings</div>
                </div>


                <div className={styles.content}>
                    <OrderCard closed={false}/>
                    <OrderCard closed={true}/>
                </div>
            </div>
        </div>
    );
};

export default MailingsPage;
