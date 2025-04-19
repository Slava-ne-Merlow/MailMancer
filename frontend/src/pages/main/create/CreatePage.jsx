import Header from "../../../components/Header/Header";
import Sidebar from "../../../components/Sidebar/Sidebar";
import React from "react";
import styles from "./CreatePage.module.css";

const CreatePage = () => {
    return (
        <div className="main-page">
            <Header />

            <Sidebar selected="create" />
            <div className="main-content">
                <div className="content-header">
                    <div className="text">Create Mailing</div>
                </div>
                <div className={styles.content}>
                    <div className={styles.card}>

                    </div>
                </div>
                </div>
            </div>
            );
            };

            export default CreatePage;
