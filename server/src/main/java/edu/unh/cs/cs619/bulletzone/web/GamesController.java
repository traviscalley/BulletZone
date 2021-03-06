package edu.unh.cs.cs619.bulletzone.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClientException;
import javax.servlet.http.HttpServletRequest;
import edu.unh.cs.cs619.bulletzone.Events.PlayerUtilities;
import edu.unh.cs.cs619.bulletzone.Events.TankUtilities;
import edu.unh.cs.cs619.bulletzone.model.Direction;
import edu.unh.cs.cs619.bulletzone.model.IllegalTransitionException;
import edu.unh.cs.cs619.bulletzone.model.LimitExceededException;
import edu.unh.cs.cs619.bulletzone.model.PlayableObject;
import edu.unh.cs.cs619.bulletzone.model.TankDoesNotExistException;
import edu.unh.cs.cs619.bulletzone.repository.GameRepository;
import edu.unh.cs.cs619.bulletzone.util.BooleanWrapper;
import edu.unh.cs.cs619.bulletzone.util.GridWrapper;
import edu.unh.cs.cs619.bulletzone.util.LongWrapper;

/**
 * Games Controller w/repository
 * methods have not been used as of yet
 */

@RestController
@RequestMapping(value = "/games")
class GamesController {

    private static final Logger log = LoggerFactory.getLogger(GamesController.class);

    private final GameRepository gameRepository;

    @Autowired
    public GamesController(GameRepository gameRepository) {
        this.gameRepository = gameRepository;
    }

    @RequestMapping(method = RequestMethod.POST, value = "/{selected}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    ResponseEntity<LongWrapper> join(HttpServletRequest request, @PathVariable boolean selected) {
        PlayableObject player;
        try {
            gameRepository.setSelectBool(selected);
            player = gameRepository.join(request.getRemoteAddr());
            log.info("Player joined: tankId={} IP={}", player.getId(), request.getRemoteAddr());

            return new ResponseEntity<LongWrapper>(
                    new LongWrapper(player.getId()),
                    HttpStatus.CREATED
            );
        } catch (RestClientException e) {
            e.printStackTrace();
        }
        return null;
    }

    @RequestMapping(method = RequestMethod.GET, value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public
    @ResponseBody
    ResponseEntity<GridWrapper> grid() {
        return new ResponseEntity<GridWrapper>(new GridWrapper(gameRepository.getGrid()), HttpStatus.ACCEPTED);
    }

    @RequestMapping(method = RequestMethod.PUT, value = "{tankId}/turn/{direction}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.ACCEPTED)
    ResponseEntity<BooleanWrapper> turn(@PathVariable long tankId, @PathVariable byte direction)
            throws TankDoesNotExistException, LimitExceededException, IllegalTransitionException {
        return new ResponseEntity<BooleanWrapper>(
                new BooleanWrapper(PlayerUtilities.turn(tankId, Direction.fromByte(direction))),
                HttpStatus.ACCEPTED
        );
    }

    @RequestMapping(method = RequestMethod.PUT, value="/select/{selected}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<BooleanWrapper> select(@PathVariable boolean isTank)
    {
        gameRepository.setSelectBool(isTank);
        return new ResponseEntity<BooleanWrapper>(
                new BooleanWrapper(true), HttpStatus.ACCEPTED
        );
    }

    @RequestMapping(method = RequestMethod.PUT, value = "{tankId}/move/{direction}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.ACCEPTED)
    ResponseEntity<BooleanWrapper> move(@PathVariable long tankId, @PathVariable byte direction)
            throws TankDoesNotExistException, LimitExceededException, IllegalTransitionException {
        return new ResponseEntity<BooleanWrapper>(
                new BooleanWrapper(PlayerUtilities.move(tankId, Direction.fromByte(direction))),
                HttpStatus.ACCEPTED
        );
    }

    @RequestMapping(method = RequestMethod.PUT, value = "{tankId}/fire/{bulletType}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.ACCEPTED)
    ResponseEntity<BooleanWrapper> fire(@PathVariable long tankId, @PathVariable int bulletType)
            throws TankDoesNotExistException, LimitExceededException {
        return new ResponseEntity<BooleanWrapper>(
                new BooleanWrapper(PlayerUtilities.fire(tankId, bulletType)),
                HttpStatus.ACCEPTED
        );
    }

    @RequestMapping(method = RequestMethod.PUT, value = "{tankId}/fire", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.ACCEPTED)
    ResponseEntity<BooleanWrapper> fire(@PathVariable long tankId)
            throws TankDoesNotExistException, LimitExceededException {
        return new ResponseEntity<BooleanWrapper>(
                new BooleanWrapper(PlayerUtilities.fire(tankId, 1)),
                HttpStatus.ACCEPTED
        );
    }

    @RequestMapping(method = RequestMethod.PUT, value = "{tankId}/eject/0", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.ACCEPTED)
    ResponseEntity<BooleanWrapper> eject(@PathVariable long tankId)
            throws TankDoesNotExistException {
        return new ResponseEntity<BooleanWrapper>(
                new BooleanWrapper(PlayerUtilities.ejectSoldier(tankId)),
                HttpStatus.ACCEPTED
        );
    }

    @RequestMapping(method = RequestMethod.PUT, value = "{tankId}/ejectP", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.ACCEPTED)
    ResponseEntity<BooleanWrapper> ejectP(@PathVariable long tankId)
            throws TankDoesNotExistException {
        return new ResponseEntity<BooleanWrapper>(
                new BooleanWrapper(TankUtilities.ejectPowerup(tankId)),
                HttpStatus.ACCEPTED
        );
    }

    @RequestMapping(method = RequestMethod.DELETE, value = "{tankId}/leave", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.ACCEPTED)
    HttpStatus leave(@PathVariable long tankId)
            throws TankDoesNotExistException {
        //System.out.println("Games Controller leave() called, tank ID: "+tankId);
        gameRepository.leave(tankId);
        return HttpStatus.ACCEPTED;
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    String handleBadRequests(Exception e) {
        return e.getMessage();
    }
}
