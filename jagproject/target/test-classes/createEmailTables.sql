USE EMAILAPP;
SET FOREIGN_KEY_CHECKS = 0;
DROP TABLE IF EXISTS Folder;
DROP TABLE IF EXISTS Email;
DROP TABLE IF EXISTS Address;
DROP TABLE IF EXISTS Attachments;
DROP TABLE IF EXISTS EmailToAddress;
SET FOREIGN_KEY_CHECKS = 1;

CREATE TABLE Folder(
    FolderId INT NOT NULL auto_increment,
    FolderName VARCHAR(50) NOT NULL DEFAULT '',
    PRIMARY KEY(FolderId)
);


CREATE TABLE Email(
    EmailId INT NOT NULL auto_increment,
    FolderId INT NOT NULL,
    FromAddress VARCHAR(320) NOT NULL DEFAULT '',
    SentDate TIMESTAMP, 
    ReceiveDate TIMESTAMP, 
    Subject VARCHAR(100) NOT NULL DEFAULT '',
    Message TEXT(3000),
    HtmlMessage TEXT(5000),
    PRIMARY KEY(EmailId),
    CONSTRAINT fk_email_folder FOREIGN KEY (FolderId) REFERENCES Folder(FolderId) ON DELETE CASCADE
);


CREATE TABLE Address(
    AddressId INT NOT NULL auto_increment,
    EmailAddress VARCHAR(255) NOT NULL DEFAULT '',
    PRIMARY KEY (AddressId)
);



CREATE TABLE Attachments(
    FileId INT NOT NULL auto_increment,
    EmailId INT NOT NULL,
    FileName VARCHAR(255) NOT NULL DEFAULT '',
    ContentId VARCHAR(255),
    FileContent MEDIUMBLOB,
    PRIMARY KEY (FileId),
    CONSTRAINT fk_email_attachments FOREIGN KEY (EmailId) REFERENCES Email(EmailId) ON DELETE CASCADE
);


CREATE TABLE EmailToAddress(
    EmailId INT NOT NULL,
    AddressId INT NOT NULL,
    RecipientType VARCHAR(3) NOT NULL DEFAULT '',
    CONSTRAINT fk_email_id FOREIGN KEY (EmailId) REFERENCES Email(EmailId) ON DELETE CASCADE,
    CONSTRAINT fk_addressid FOREIGN KEY (AddressId) REFERENCES Address(AddressId) ON DELETE CASCADE
);


INSERT INTO Folder(FolderName) VALUES
("Inbox"), 
("Sent"), 
("Draft");

INSERT INTO Email(FolderId, FromAddress, Subject, Message, HtmlMessage) VALUES
(2, "xinjia.caoxin@gmail.com","subject1", "Inbox Msg", ""),
(2, "xinjia.caoxin@gmail.com","subject1_1", "Inbox Msg", ""),
(2, "xinjia1.cao@gmail.com","subject2", "Sent Msg", ""),
(1, "xinjia1.cao@gmail.com","subject2_2", "Received Msg", ""),
(2, "xinjia2.cao@gmail.com", "subject3", "Sent Msg", ""),
(3,"xinjia3.cao@gmail.com","subject4", "Draft Msg", ""),
(1,"xinjia3.cao@gmail.com","subject_test","message_test","html_test");

INSERT INTO Address(EmailAddress) VALUES
("xinjia.caoxin@gmail.com"), 
("xinjia1.cao@gmail.com"), 
("xinjia2.cao@gmail.com"), 
("xinjia3.cao@gmail.com"), 
("xinjia4.cao@gmail.com");

INSERT INTO Attachments(FileName, EmailId) VALUES
("img1.png", 1),
("img2.png", 1), 
("img3-embedded.png", 3),
("tree.png", 3),
("flower.png", 4),
("dog-embedded.png", 5);

INSERT INTO EmailToAddress(EmailId, AddressId,RecipientType) VALUES
(1,1,"To"),
(1,3,"CC"),
(2,4,"CC"),
(3,2,"To"),
(3,2,"BCC"),
(4,3,"To"),
(5,4,"CC");



