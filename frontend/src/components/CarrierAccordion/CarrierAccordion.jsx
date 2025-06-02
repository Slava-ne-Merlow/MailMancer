import React, {useState} from "react";
import {ChevronDown} from "lucide-react";
import styles from "./CarrierAccordion.module.css";
import download from "../../assets/icons/download.svg";
import upload from "../../assets/icons/upload.svg";
import trash from "../../assets/icons/trash.svg";
import EditableCell from "../CustomEditableCell/EditableCell";
import userStore from "../../store/UserStore";


const CarrierAccordion = ({withExtraColumns = true, data, onSuccess}) => {
    console.log(data)
    const [open, setOpen] = useState(false);
    const toggleAccordion = () => {
        setOpen((prev) => !prev);
    };

    const uploadFile = async (file, type) => {
        const formData = new FormData();
        formData.append("file", file);

        try {
            const response = await fetch(
                `http://localhost:8080/api/v1/carriers/file?name=${data.name}&type=${type}`,
                {
                    method: "POST",
                    headers: {
                        Authorization: `Bearer ${userStore.token}`,
                    },
                    body: formData,
                }
            );

            if (!response.ok) throw new Error("Upload failed");
            onSuccess();
        } catch (error) {
            console.error("Ошибка при загрузке файла:", error);
            alert("Ошибка при загрузке файла");
        }
    };
    const deleteFile = async (fileName, type) => {
        try {
            const response = await fetch(
                `http://localhost:8080/api/v1/carriers/file?name=${data.name}&type=${type}&fileName=${fileName}`,
                {
                    method: "DELETE",
                    headers: {
                        Authorization: `Bearer ${userStore.token}`,
                    },
                }
            );

            if (!response.ok) throw new Error("Delete failed");
            onSuccess();
        } catch (error) {
            console.error("Ошибка при удалении файла:", error);
            alert("Ошибка при удалении файла");
        }
    };
    const downloadFile = async (fileName, type) => {
        try {
            const response = await fetch(
                `http://localhost:8080/api/v1/carriers/file?name=${data.name}&type=${type}&fileName=${fileName}`,
                {
                    method: "GET",
                    headers: {
                        Authorization: `Bearer ${userStore.token}`,
                    },
                }
            );

            if (!response.ok) throw new Error("Download failed");

            const blob = await response.blob();
            const url = window.URL.createObjectURL(blob);
            const link = document.createElement("a");
            link.href = url;
            link.setAttribute("download", fileName);
            document.body.appendChild(link);
            link.click();
            link.remove();
        } catch (error) {
            console.error("Ошибка при скачивании файла:", error);
            alert("Ошибка при скачивании файла");
        }
    };


    return (
        <div className={styles.wrapper}>
            <div
                className={`${styles.card} ${open ? styles.cardOpen : ""}`}
                onClick={toggleAccordion}
            >
                <ChevronDown className={`${styles.icon} ${open ? styles.iconOpen : ""}`}/>
                <div className={styles.text}>{data.name.toString().toUpperCase()}</div>
            </div>

            <div className={`${styles.content} ${open ? styles.contentOpen : ""}`}>
                <div className={styles.innerContent}>
                    <div className={styles.tableWrapper}>
                        <table className={styles.table}>
                            <thead>
                            <tr>
                                <th className={styles.col1}>NAME</th>
                                <th className={styles.col1}>EMAIL</th>
                                <th className={styles.col1}>PHONE NUMBER</th>
                                {withExtraColumns && (
                                    <>
                                        <th className={styles.col2}>COMMENT</th>
                                        <th className={styles.col3}>FILES</th>
                                    </>
                                )}
                            </tr>
                            </thead>
                            <tbody>
                            {data.representatives.map((item, index) => (
                                <tr key={index}>
                                    <EditableCell
                                        initialValue={item.name}
                                        type="representative"
                                        id={item.id}
                                        field="name"
                                    />
                                    <EditableCell
                                        initialValue={item.email}
                                        type="representative"
                                        id={item.id}
                                        field="email"
                                    />
                                    <EditableCell
                                        initialValue={item.phoneNumber}
                                        type="representative"
                                        id={item.id}
                                        field="phoneNumber"
                                    />

                                    {index === 0 && withExtraColumns && (
                                        <>
                                            <EditableCell
                                                initialValue={data.comment}
                                                type="company"
                                                id={data.name}
                                                field="comment"
                                                rowSpan={data.representatives.length}
                                                className={styles.mergedCell1}
                                            />
                                            <td
                                                rowSpan={data.representatives.length}
                                                className={styles.mergedCell2}
                                                style={{borderBottom: "none",}}
                                            >
                                                <div className={styles.fullCell}>
                                                    <div className={styles.miniTableHeader}>APPLICATION</div>

                                                    <div className={styles.miniTableContent}
                                                         style={{borderBottom: "1px solid #e5e5e5"}}>
                                                        {data.application === null ? (
                                                            <label>
                                                                <img className={styles.img} alt="upload" src={upload}
                                                                     title="Upload file"/>
                                                                <input
                                                                    type="file"
                                                                    style={{display: "none"}}
                                                                    onChange={(e) => uploadFile(e.target.files[0], "application")}
                                                                />
                                                            </label>
                                                        ) : (
                                                            <>
                                                                <img className={styles.img} alt="download"
                                                                     src={download}
                                                                     title={"Download " + data.application}
                                                                     onClick={() => downloadFile(data.application, "application")}
                                                                />
                                                                <img className={styles.img} alt="trash" src={trash}
                                                                     title="Delete file"
                                                                     onClick={() => deleteFile(data.application, "application")}
                                                                />
                                                            </>
                                                        )}
                                                    </div>
                                                    <div className={styles.miniTableHeader}>CONTRACT</div>
                                                    <div className={styles.miniTableContent}>
                                                        {data.contract === null ? (
                                                            <label>
                                                                <img className={styles.img} alt="upload" src={upload}
                                                                     title="Upload file"/>
                                                                <input
                                                                    type="file"
                                                                    style={{display: "none"}}
                                                                    onChange={(e) => uploadFile(e.target.files[0], "contract")}
                                                                />
                                                            </label>
                                                        ) : (
                                                            <>
                                                                <img className={styles.img} alt="download"
                                                                     src={download}
                                                                     title={"Download " + data.contract}
                                                                     onClick={() => downloadFile(data.contract, "contract")}
                                                                />
                                                                <img className={styles.img} alt="trash" src={trash}
                                                                     title="Delete file"
                                                                     onClick={() => deleteFile(data.contract, "contract")}
                                                                />
                                                            </>
                                                        )}
                                                    </div>
                                                </div>
                                            </td>
                                        </>
                                    )}
                                </tr>
                            ))}
                            </tbody>
                        </table>
                    </div>
                </div>
            </div>
        </div>
    );
};

export default CarrierAccordion;
