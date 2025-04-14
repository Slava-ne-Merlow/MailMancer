import Header from "../../../components/Header/Header";
import Sidebar from "../../../components/Sidebar/Sidebar";
import React from "react";

const CreatePage = () => {
    return (
        <div className="main-page">
            <Header />

            <Sidebar selected="create" />
            <div className="main-content">
                <div style={{fontFamily: "Nunito Sans-Bold"}}>Авто</div>
            </div>
        </div>
    );
};

export default CreatePage;
