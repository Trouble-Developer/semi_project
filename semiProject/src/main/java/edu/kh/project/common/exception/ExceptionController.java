package edu.kh.project.common.exception;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.resource.NoResourceFoundException;


@ControllerAdvice 
public class ExceptionController {

	/** dev. 안재훈
	 * 404 에러 발생시 404 페이지로 forward
	 * @return
	 */
	@ExceptionHandler(NoResourceFoundException.class)
	// 404 오류
	public String notFound(Model model, Exception e) {
		model.addAttribute("e", e);
		return "error/404";
	}

	/** dev. 안재훈
	 * 500 에러 발생시 500 페이지로 forward
	 * @param model
	 * @param e
	 * @return
	 */
	@ExceptionHandler(Exception.class)
	public String allExceptionHandler(Model model, Exception e) {
		// 500 error
		e.printStackTrace();
		model.addAttribute("e", e);
		return "error/500"; 
	}

}
