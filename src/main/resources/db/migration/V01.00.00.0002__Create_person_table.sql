/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 * Author:  mlarr
 * Created: 10 feb. 2019
 */

DROP TABLE IF EXISTS PEOPLE;
COMMIT;

BEGIN;
create table PEOPLE (
    ID int not null,
    NAME varchar(100) not null
);

insert into PEOPLE (ID, NAME) values (1, 'Axel');
insert into PEOPLE (ID, NAME) values (2, 'Mr. Foo');
insert into PEOPLE (ID, NAME) values (3, 'Ms. Bar');

commit;