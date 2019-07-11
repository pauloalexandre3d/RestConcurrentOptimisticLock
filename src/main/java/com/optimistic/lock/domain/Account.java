package com.optimistic.lock.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Version;

import lombok.Data;

@Entity
@Data
public class Account {

	@Id
    @GeneratedValue
    private Long id;
	
	@Version
    private Long version;
	
    private Long balance;
}
