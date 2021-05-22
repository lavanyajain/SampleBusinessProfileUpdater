create table profile (
  tax_id varchar(20) not null,
  company_name varchar(25) not null,
  legal_name varchar (25) not null,
  address_line1 varchar(20),
  address_line2 varchar(20),
  address_city varchar(30) not null,
  address_state varchar(30) not null,
  address_zip varchar(30) not null,
  address_country varchar(30) not null,
  legal_address_line1 varchar(20),
  legal_address_line2 varchar(20),
  legal_address_city varchar(30) not null,
  legal_address_state varchar(30) not null,
  legal_address_zip varchar(30) not null,
  legal_address_country varchar(30) not null,
  email varchar(30) not null,
  website varchar(30) not null,
  primary key(tax_id)
);

create table subscription (
  tax_id varchar(20) not null,
  subscription_id int not null auto_increment,
  subscription varchar(30) not null,
  FOREIGN KEY (`tax_id`) REFERENCES `profile`(`tax_id`)
   ON DELETE CASCADE
);

insert into profile (tax_id, company_name, legal_name, address_line1, address_line2, address_city, address_state, address_zip, address_country, legal_address_line1, legal_address_line2, legal_address_city, legal_address_state, legal_address_zip, legal_address_country, email, website) values ('ANYPL2977R' , 'ABC Technologies', 'ABC Technologies', 'Whitefield', 'Whitefield', 'Bengaluru', 'Karnataka', '560066', 'India', 'Whitefield', 'Whitefield', 'Bengaluru', 'Karnataka', '560066', 'India', 'lavanya.pjain@gmail.com', 'www.abc.co.in');
insert into profile (tax_id, company_name, legal_name, address_line1, address_line2, address_city, address_state, address_zip, address_country, legal_address_line1, legal_address_line2, legal_address_city, legal_address_state, legal_address_zip, legal_address_country, email, website) values ('ANYPL2976R' , 'ABC Technologies', 'ABC Technologies', 'Whitefield', 'Whitefield', 'Bengaluru', 'Karnataka', '560066', 'India', 'Whitefield', 'Whitefield', 'Bengaluru', 'Karnataka', '560066', 'India', 'lavanya.pjain@gmail.com', 'www.abc.co.in');


insert into subscription(tax_id, subscription_id, subscription) values('ANYPL2977R', null, 'accounting');
insert into subscription(tax_id, subscription_id, subscription) values('ANYPL2977R', null, 'payroll');



