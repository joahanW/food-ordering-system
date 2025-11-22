package com.metrodata.common.domain.entity;

import lombok.AllArgsConstructor;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@AllArgsConstructor
public abstract class AggregateRoot<ID> extends BaseEntity<ID>{
}
