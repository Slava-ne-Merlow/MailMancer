import Header from "../../../components/Header/Header";
import Sidebar from "../../../components/Sidebar/Sidebar";
import React, {useEffect, useState} from "react";
import CarrierAccordion from "../../../components/CarrierAccordion/CarrierAccordion";
import styles from "./CarriersPage.module.css";
import addIcon from "../../../assets/icons/add.svg";
import CarrierForm from "../../../components/CarrierForm/CarrierForm";
import userStore from "../../../store/UserStore";

const CarriersPage = () => {
    const [accordionList, setAccordionList] = useState([]);
    const [showForm, setShowForm] = useState(false);
    const contentClass = showForm ? styles.contentGrid : styles.contentFull;

    const setShowForm1 = () => {
        setShowForm((prev) => !prev);
    };
    useEffect(() => {
        fetchCarriers();
    }, []);

    const fetchCarriers = async () => {
        try {
            const response = await fetch("http://localhost:8080/api/v1/carriers", {
                headers: {
                    Authorization: userStore.token,
                },
            });
            const data = await response.json();

            setAccordionList(data);
        } catch (error) {
            console.error("Ошибка загрузки данных о перевозчиках:", error);
        }
    };
    const handleFormSubmit = () => {
        setShowForm(false);
        fetchCarriers();
    };
    return (
        <div className="main-page">
            <Header/>
            <Sidebar selected="carriers"/>
            <div className="main-content">
                <div className="content-header">
                    <div className="text">Carriers</div>
                    <div className={styles.button} onClick={() => setShowForm1()}>
                        <img className={styles.icon} src={addIcon} alt="add"/>
                    </div>
                </div>

                <div className={contentClass}>
                    <div className={styles.accordionList}>
                        {accordionList.map((accordion, index) => (
                            <CarrierAccordion
                                key={index}
                                withExtraColumns={!showForm}
                                data={accordion}
                                onSuccess={handleFormSubmit}
                            />
                        ))}
                    </div>

                    {showForm && <CarrierForm onSuccess={handleFormSubmit}/>}
                </div>
            </div>
        </div>
    );
};

export default CarriersPage;
