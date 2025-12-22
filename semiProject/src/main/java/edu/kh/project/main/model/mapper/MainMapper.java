package edu.kh.project.main.model.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import edu.kh.project.main.model.Member;

@Mapper
public interface MainMapper {


	Member testData();
}
