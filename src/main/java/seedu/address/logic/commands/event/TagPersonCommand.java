package seedu.address.logic.commands.event;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.commands.event.util.CommandUtil.createEditedEvent;
import static seedu.address.logic.commands.event.util.CommandUtil.createEditedPerson;

import java.util.List;

import seedu.address.commons.core.Messages;
import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.CommandResult;
import seedu.address.logic.commands.EditCommand;
import seedu.address.logic.commands.UndoableCommand;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.event.Event;
import seedu.address.model.event.ReadOnlyEvent;
import seedu.address.model.person.Person;
import seedu.address.model.person.ReadOnlyPerson;
import seedu.address.model.person.UniquePersonList;
import seedu.address.model.person.exceptions.DuplicatePersonException;

/**
 * Tags a person in an event.
 */
public class TagPersonCommand extends UndoableCommand {
    public static final String COMMAND_WORD = "eventtag";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Tags a person by the index number used in the last "
            + "person listing to an event by the index number used in the last event listing. "
            + "Existing values will be overwritten by the input values.\n"
            + "Parameters: INDEX_OF_EVENT (must be a positive integer) INDEX_OF_PERSON (must be a positive integer) "
            + "Example: " + COMMAND_WORD + " 1 2 ";

    public static final String MESSAGE_TAGPERSON_SUCCESS = "%1$s\n has been tag in %2$s";
    public static final String MESSAGE_DUPLIATE_TAG = "This person has already been tagged in the event.";

    private final Index eventIndex;
    private final Index personIndex;

    private EditEventCommand.EditEventDescriptor editEventDescriptor;
    private EditCommand.EditPersonDescriptor editPersonDescriptor;

    /**
     * @param eventIndex   index of the event in the filtered event list to add tag
     * @param personIndex   index of the person in the filtered person list to be tagged in the event
     */
    public TagPersonCommand(Index eventIndex, Index personIndex) {
        requireNonNull(eventIndex);
        requireNonNull(personIndex);

        this.eventIndex = eventIndex;
        this.personIndex = personIndex;
        this.editEventDescriptor = new EditEventCommand.EditEventDescriptor();
    }

    @Override
    public CommandResult executeUndoableCommand() throws CommandException {

        List<ReadOnlyEvent> lastShownEventList = model.getFilteredEventList();

        if (eventIndex.getZeroBased() >= lastShownEventList.size()) {
            throw new CommandException(Messages.MESSAGE_INVALID_EVENT_DISPLAYED_INDEX);
        }

        List<ReadOnlyPerson> lastShownPersonList = model.getFilteredPersonList();

        if ((personIndex.getZeroBased() >= lastShownPersonList.size())) {
            throw new CommandException(Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
        }

        ReadOnlyEvent eventToTag = lastShownEventList.get(eventIndex.getZeroBased());
        ReadOnlyPerson personToTag = lastShownPersonList.get(personIndex.getZeroBased());

        Event event = createEditedEvent(eventToTag, editEventDescriptor);
        Person person = createEditedPerson(personToTag, editPersonDescriptor);

        UniquePersonList eventPersonList = new UniquePersonList();

        try {
            eventPersonList.setPersons(event.getPersonList());
            eventPersonList.add(person);
        } catch (DuplicatePersonException dpe) {
            throw new CommandException(MESSAGE_DUPLIATE_TAG);
        }
        //upmodel.updateFilteredPersonList(PREDICATE_SHOW_ALL_PERSONS);
        return new CommandResult(String.format(MESSAGE_TAGPERSON_SUCCESS, person, event));
    }

    @Override
    public boolean equals(Object other) {
        // short circuit if same object
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof TagPersonCommand)) {
            return false;
        }

        // state check
        TagPersonCommand e = (TagPersonCommand) other;
        return eventIndex.equals(e.eventIndex) && personIndex.equals(e.personIndex);
    }

}
