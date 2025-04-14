import Header from "../../../components/Header/Header";
import Sidebar from "../../../components/Sidebar/Sidebar";
import React from "react";

const MailingsPage = () => {
    return (
        <div className="main-page">
            <Header/>

            <Sidebar selected="mailings"/>
            <div className="main-content">
                <div>Mailings</div>
            </div>
        </div>
    );
};

export default MailingsPage;
