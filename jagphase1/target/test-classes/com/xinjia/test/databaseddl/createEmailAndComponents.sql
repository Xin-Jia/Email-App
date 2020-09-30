/**
 * Author:  Xin Jia Cao
 * Created: Sep. 29, 2020
 */

CREATE TABLE Email(
    EmailId INT NOT NULL auto_increment,
    SenderAddress VARCHAR(255),
    ToField VARCHAR(255),
    CCField VARCHAR(255),
    BCCField VARCHAR(255),
    Subject VARCHAR(255),
    Message VARCHAR(255),
    Attachements BLOB,
    PRIMARY KEY(EmailId)
);

CREATE TABLE Address(
    EmailAddress VARCHAR(255),
    PRIMARY KEY (EmailAddress)
);

CREATE TABLE EmailToAddress(
    EmailAddress VARCHAR(255),
    ToField VARCHAR(255),
    CONSTRAINT fksentemail FOREIGN KEY (EmailId) REFERENCES Email(EmailId),
    CONSTRAINT fkemail FOREIGN KEY (EmailAddress) REFERENCES Address(EmailAddress),
    PRIMARY KEY (ToField)
);

CREATE TABLE Attachements(
    FileName VARCHAR(255),
    FileType VARCHAR(255),
    PRIMARY KEY (FileName)
);

CREATE TABLE EmailToAttachments(
    EmailAddress VARCHAR(255),
    FileName VARCHAR(255),
    CONSTRAINT fksentemail FOREIGN KEY (SenderAddress) REFERENCES Email(SenderAddress),
    CONSTRAINT fkattachements FOREIGN KEY (FileName) REFERENCES Attachements(FileName),
    PRIMARY KEY (ToField)
);