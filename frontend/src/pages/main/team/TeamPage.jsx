import React, {useEffect, useState} from "react";
import Header from "../../../components/Header/Header";
import Sidebar from "../../../components/Sidebar/Sidebar";
import userStore from "../../../store/UserStore";
import styles from "./TeamPage.module.css";
import avatar from "../../../assets/icons/avatar.svg";

const TeamPage = () => {
    const [url, setUrl] = useState([]);
    const [teamMembers, setTeamMembers] = useState([]);
    const [toastVisible, setToastVisible] = useState(false);

    const getData = async () => {
        try {
            const response = await fetch("http://localhost:8080/api/v1/team", {
                method: "GET",
                headers: {
                    "Content-Type": "application/json",
                    "Authorization": `${userStore.token}`,
                },
            });

            const data = await response.json();

            if (!response.ok) {
                console.log("Ошибка запроса: " + (data.message || response.status));
                return;
            }

            setTeamMembers(data);
        } catch (error) {
            console.error("Ошибка сети:", error);
        }
    };

    const getUrl = async () => {
        try {
            const response = await fetch("http://localhost:8080/api/v1/generate-invite", {
                method: "GET",
                headers: {
                    "Content-Type": "application/json",
                    "Authorization": `${userStore.token}`,
                },
            });

            const data = await response.json();

            if (!response.ok) {
                console.log("Ошибка генерации ссылки: " + (data.message || response.status));
                return;
            }

            if (data.url) {
                return data.url; // вот тут было нужно
            }
        } catch (error) {
            console.error("Ошибка сети при генерации приглашения:", error);
        }
    };


    const copyTextToClipboard = async (text) => {
        try {
            await navigator.clipboard.writeText(text);
            console.log('Текст успешно скопирован в буфер обмена!');
            showToast();
        } catch (err) {
            console.error('Ошибка:', err);
        }
    };


    const showToast = () => {
        setToastVisible(true);
        setTimeout(() => {
            setToastVisible(false);
        }, 3000); // уведомление исчезнет через 3 секунды
    };

    useEffect(() => {
        const fetchData = async () => {
            await getData();
            const url = await getUrl();
            setUrl(url);
        };

        fetchData();
    }, []);


    return (
        <div className="main-page">
            <Header />
            <Sidebar selected="team" />

            <div className="main-content">
                <div className="content-header">
                    <div className="text">Team</div>

                    {userStore.user.role === "HEAD" && <div className={styles.button}>
                        <div className={styles.text}  onClick={() => copyTextToClipboard(url)}>Invite Member</div>
                    </div>}
                </div>

                <div className={styles.cardsGrid}>
                    {teamMembers.map((member, index) => (
                        <div key={index} className={styles.card}>
                            <img className={styles.avatar} alt="Avatar" src={avatar}/>
                            <div className={styles.name}>{member.name}</div>
                            <div className={styles.role}>{member.role}</div>
                            <div className={styles.email}>{member.email}</div>
                        </div>
                    ))}
                </div>
                {toastVisible && (
                    <div className={styles.toast}>
                        Invite link copied to clipboard!
                    </div>
                )}
            </div>
        </div>
    );
};

export default TeamPage;
