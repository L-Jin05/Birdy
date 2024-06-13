package com.keduit.bird.controller;



import com.keduit.bird.dto.CommentDTO;
import com.keduit.bird.service.CommentService;
import com.keduit.bird.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.security.Principal;
import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/comment")
public class CommentController {
    private final CommentService commentService;
    private final MemberService memberService;

    @PostMapping("/save")
    public ResponseEntity save(@ModelAttribute CommentDTO commentDTO, Principal principal) {
        System.out.println("commentDTO = " + commentDTO);
        String memberEmail = principal.getName();
        String memberName = memberService.findMemberNameByMemberEmail(memberEmail);
        commentDTO.setCommentWriter(memberName);
        Long saveResult = commentService.save(commentDTO);
        System.out.println(commentDTO);
        if (saveResult != null) {
            //작성 성공
            List<CommentDTO> commentDTOList = commentService.findAll(commentDTO.getBoardId());
            return new ResponseEntity<>(commentDTOList, HttpStatus.OK);
        } else {
            return new ResponseEntity<>("해당 게시글이 존재하지 않습니다.", HttpStatus.NOT_FOUND);
        }
    }

}