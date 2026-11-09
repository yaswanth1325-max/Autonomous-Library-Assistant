package com.libraryrobot.task;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface RobotTaskRepository extends JpaRepository<RobotTask, Long> {
    List<RobotTask> findAllByOrderByCreatedAtDesc();
}
