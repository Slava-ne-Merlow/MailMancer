import Header from "../../../components/Header/Header";
import Sidebar from "../../../components/Sidebar/Sidebar";
import styles from "./MailingsPage.module.css";
import React from "react";
import OrderCard from "../../../components/OrderCard/OrderCard";
import { useSearchParams} from "react-router-dom";
import OrderDetailsPage from "../order/OrderDetailsPage";

const MailingsPage = () => {
    const [searchParams] = useSearchParams();
    const orderId = searchParams.get("orderId");


    return (
        <div className="main-page">
            <Header />
            <Sidebar selected="mailings" />

            <div className="main-content">
                <div className="content-header">
                    <div className="text">Mailings</div>
                </div>


                {orderId ? (
                    <div className={styles.orderPage}>
                        { orderId ? (

                            <OrderDetailsPage/>
                        ) : (
                            <div>Нет данных о заказе</div>
                        )}
                    </div>
                ) : (
                    <div className={styles.content}>
                        <OrderCard closed={false}/>
                        <OrderCard closed={true}/>
                    </div>
                )}

            </div>
        </div>
    );
};


export default MailingsPage;
