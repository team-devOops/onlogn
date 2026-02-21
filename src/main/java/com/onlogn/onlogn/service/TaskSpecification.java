package com.onlogn.onlogn.service;

import com.onlogn.onlogn.entity.TaskEntity;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public final class TaskSpecification {

    private TaskSpecification() {
    }

    public static Specification<TaskEntity> build(
            UUID ownerUserId,
            String status,
            String visibility,
            String groupId,
            LocalDate dueDateFrom,
            LocalDate dueDateTo
    ) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (ownerUserId != null) {
                predicates.add(cb.equal(root.get("ownerUserId"), ownerUserId));
            }

            if (status != null && !status.isBlank()) {
                predicates.add(cb.equal(root.get("status"), status));
            }

            if (visibility != null && !visibility.isBlank()) {
                predicates.add(cb.equal(root.get("visibility"), visibility));
            }

            if (groupId != null) {
                if ("null".equalsIgnoreCase(groupId)) {
                    predicates.add(cb.isNull(root.get("groupId")));
                } else {
                    predicates.add(cb.equal(root.get("groupId"), UUID.fromString(groupId)));
                }
            }

            if (dueDateFrom != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("dueDate"), dueDateFrom));
            }

            if (dueDateTo != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("dueDate"), dueDateTo));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
