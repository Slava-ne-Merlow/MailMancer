import Header from "../../components/Header/Header";
import Sidebar from "../../components/Sidebar/Sidebar";
import React from "react";

const TeamPage = () => {
    return (
        <div className="main-page">
            <Header />

            <Sidebar  selected="team"  />

        </div>
    );
};

export default TeamPage;
