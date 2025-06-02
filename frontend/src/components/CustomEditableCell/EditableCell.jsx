import {useEffect, useRef} from "react";
import userStore from "../../store/UserStore";

const EditableCell = ({
                          initialValue,
                          type,
                          id,
                          field,
                          onSave,
                          rowSpan,
                          className,
                      }) => {
    const cellRef = useRef(null);
    const originalValue = useRef(initialValue);
    useEffect(() => {
        if (cellRef.current) {
            cellRef.current.innerText = initialValue;
        }
    }, [initialValue]);
    const handleBlur = async () => {
        const newValue = cellRef.current.innerText;
        if (newValue !== originalValue.current) {
            const confirmSave = window.confirm("Save the changes?");
            if (confirmSave) {
                try {
                    const response = await fetch("http://localhost:8080/api/v1/carriers/update-field", {
                        method: "PUT",
                        headers: {
                            "Content-Type": "application/json",
                            Authorization: userStore.token,
                        },
                        body: JSON.stringify({
                            type,
                            id: type === "company" ? undefined : id,
                            name: type === "company" ? id : undefined,
                            field,
                            value: newValue,
                        }),
                    });

                    if (!response.ok) throw new Error("Ошибка при обновлении");

                    originalValue.current = newValue;
                    onSave?.(newValue);
                } catch (err) {
                    alert("Ошибка при сохранении");
                    cellRef.current.innerText = originalValue.current;
                }
            } else {
                cellRef.current.innerText = originalValue.current;
            }
        }
    };

    return (
        <td
            contentEditable
            ref={cellRef}
            rowSpan={rowSpan}
            className={className}
            onBlur={handleBlur}
            style={ type === "company" ? { whiteSpace: "pre-wrap", borderBottom: "none"} : null }
        />
    );
};

export default EditableCell;