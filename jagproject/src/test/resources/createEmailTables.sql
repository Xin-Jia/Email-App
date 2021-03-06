-- Xin Jia Cao
-- DDL and DML statements for Address, Attachments, Email, EmailToAddress and Folder tables

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

INSERT INTO Email(FolderId, SentDate, ReceiveDate, FromAddress,  Subject, Message, HtmlMessage) VALUES
(2, "2020-08-13 15:05:05",null,"xinjia.caoxin@gmail.com","subject1", "Inbox Msg", "<b>hello there<b>"),
(2, "2020-08-14 04:48:12", null, "xinjia.caoxin@gmail.com","subject1_1", "Inbox Msg", ""),
(2, "2020-09-21 20:36:30", null,"xinjia1.cao@gmail.com","subject2", "Sent Msg", "<b>have a nice day<b>"),
(1, "2020-10-03 15:34:05", "2020-10-03 15:34:10","xinjia1.cao@gmail.com","subject2_2", "Received Msg", ""),
(2, "2020-10-04 22:34:54", null,"xinjia2.cao@gmail.com", "subject3", "Sent Msg", ""),
(3, null, null,"xinjia3.cao@gmail.com","subject4", "Draft Msg", ""),
(1, "2020-10-04 15:45:07", "2020-10-03 15:05:05","xinjia3.cao@gmail.com","subject_test","message_test","<p>good luck!<p>"),
(1, "2020-10-07 19:30:25", "2020-10-03 15:05:05","xinjia1.cao@gmail.com", "aliceSub","aliceMsg",""),
(2, "2020-10-07 09:45:55", null,"xinjia2.cao@gmail.com", "bob Sub","bob Msg",""),
(3, null, null,"alice123@gmail.com", "a draft", "draft msg", "<p>draft html msg<p>");

INSERT INTO Address(EmailAddress) VALUES
("xinjia.caoxin@gmail.com"), 
("xinjia1.cao@gmail.com"), 
("xinjia2.cao@gmail.com"), 
("xinjia3.cao@gmail.com"), 
("xinjia4.cao@gmail.com"),
("alice123@gmail.com"),
("bob123@gmail.com");

INSERT INTO Attachments(FileName, EmailId) VALUES
("img1.png", 1),
("img2.png", 1), 
("img3-embedded.png", 3),
("tree.png", 3),
("flower.png", 4),
("dog-embedded.png", 5),
("img3.png", 10);

INSERT INTO EmailToAddress(EmailId, AddressId,RecipientType) VALUES
(1,1,"To"),
(1,3,"CC"),
(2,4,"CC"),
(3,2,"To"),
(3,2,"BCC"),
(4,3,"To"),
(5,4,"CC"),
(6,1,"To"),
(8,6,"To"),
(8,3,"To"),
(9,1,"To"),
(9,7,"BCC"),
(10,2,"CC");



