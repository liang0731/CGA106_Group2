package com.ezticket.core.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// Annotations
@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmailDetails {
    // Class data members
    private String recipient;
    private String msgBody;
    private String subject;
    private String attachment;
}
