package com.keduit.bird.controller;

import com.keduit.bird.dto.BoardDTO;
import com.keduit.bird.dto.CommentDTO;
import com.keduit.bird.service.BoardService;
import com.keduit.bird.service.CommentService;
import com.keduit.bird.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.security.Principal;
import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/board")
public class BoardController {
    private final BoardService boardService;
    private final CommentService commentService;
    private final MemberService memberService;

    @GetMapping("/save")
    public String saveForm(Principal principal, Model model){
        String memberEmail = principal.getName();
        String adminBoardId = memberService.findMemberNameByMemberEmail(memberEmail);
        model.addAttribute("adminBoardId", adminBoardId);
        return "save";
    }

    //작성자를 해당 유저의 이름으로 자동적용시키기위해 코드 추가함.
    @PostMapping("/save")
    public String save(@ModelAttribute BoardDTO boardDTO, Principal principal) throws IOException {
        System.out.println("boardDTO = " + boardDTO);
        boardDTO.setAdminBoardId(memberService.findMemberNameByMemberEmail(principal.getName()));
        boardService.save(boardDTO);
        return "redirect:/board/paging";
    }

    @GetMapping("/list")
    public String findAll(Model model){
        List<BoardDTO> boardDTOList = boardService.findAll();
        model.addAttribute("boardList",boardDTOList);
        return "paging";
    }
    @GetMapping("/{id}")
    public String findById(@PathVariable Long id, Model model,
                           @PageableDefault(page=1) Pageable pageable,
                           Principal principal){
        //해당 게시글의 조회수를 하나 올리고 게시글 데이터를 가져와서 출력
        boardService.updateHits(id);
        BoardDTO boardDTO = boardService.findById(id);
        //댓글 작성자 가져오기//
        String memberEmail = principal.getName();
        String comment = memberService.findMemberNameByMemberEmail(memberEmail);
        model.addAttribute("commentWriter", comment);
        //댓글목록 가져오기//
        List<CommentDTO> commentDTOList = commentService.findAll(id);
        model.addAttribute("commentList",commentDTOList);

        model.addAttribute("board", boardDTO);
        model.addAttribute("page", pageable.getPageNumber());
        return "detail";
    }

    @GetMapping("/update/{id}")
    public String updateForm(@PathVariable Long id, Model model){
        BoardDTO boardDTO = boardService.findById(id);
        model.addAttribute("boardUpdate", boardDTO);
        return "update";
    }

    @PostMapping("/update")
    public String update(@ModelAttribute BoardDTO boardDTO, Model model) {
        BoardDTO board = boardService.update(boardDTO);
        model.addAttribute("board", board);
        return "detail";
//        return "redirect:/board/" + boardDTO.getId(); 조회수 오류때문에
    }

    //삭제
    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id){
        boardService.delete(id);
        return "redirect:/board/paging";
    }
    //페이지처리
    @GetMapping("/paging")
    public String paging(@PageableDefault(page = 1)Pageable pageable, Model model){
        //   pageable.getPageNumber();
        Page<BoardDTO> boardList = boardService.paging(pageable);
        int blockLimit = 3;
        int startPage = (((int)(Math.ceil((double)pageable.getPageNumber() / blockLimit))) - 1) * blockLimit + 1; // 1 4 7 10 ~~
        int endPage = ((startPage + blockLimit - 1) < boardList.getTotalPages()) ? startPage + blockLimit - 1 : boardList.getTotalPages();

        // page 갯수 20개
        // 현재 사용자가 3페이지
        // 1 2 3
        // 현재 사용자가 7페이지
        // 7 8 9
        // 보여지는 페이지 갯수 3개
        // 총 페이지 갯수 8개

        model.addAttribute("boardList", boardList);
        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);
        return "paging";
    }


}
