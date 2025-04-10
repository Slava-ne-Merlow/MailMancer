import Header from "../../components/Header/Header";
import Sidebar from "../../components/Sidebar/Sidebar";
import React from "react";

const MailingsPage = () => {
    return (
        <div className="main-page">
            <Header />

            <Sidebar selected="mailings" />

        </div>
    );
};

export default MailingsPage;
