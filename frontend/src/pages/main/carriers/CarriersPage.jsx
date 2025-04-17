import Header from "../../../components/Header/Header";
import Sidebar from "../../../components/Sidebar/Sidebar";
import React from "react";

const CarriersPage = () => {
    return (
        <div className="main-page">
            <Header/>

            <Sidebar selected="carriers"/>
            <div className="main-content">
                <div className="content-header">
                    <div className="text">Carriers</div>
                </div>
            </div>
        </div>
    );
};

export default CarriersPage;
